package wineClient

import cats.Id
import cats.effect.IO
import cats.effect.unsafe.implicits.global
import client.HttpClient
import cats.implicits._
import cats.effect._
import mocks.HttpClientMock
import org.scalamock.scalatest.MockFactory
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import vivino.VivinoWineClient
import vivino.domain.CurrencyCode.Euro
import vivino.domain.{CountryCode, Wine, WineType}
import vivino.parser.JsoupVivinoHtmlParser

import scala.io.Source

class WineClientTest extends AnyFreeSpec with Matchers with MockFactory {
  def readFile(filename: String): String = {
    val src = Source.fromResource(filename)
    src.mkString
  }

  trait Scope {
    def httpClient: HttpClient[IO]
    def vivinoHtmlParser = new JsoupVivinoHtmlParser[IO]()
    def wineClient = new VivinoWineClient[IO](vivinoHtmlParser, httpClient)
  }

  "WineClient" - {
    "getWinesByName" in new Scope {
      val map: Map[String, String] = Map("q" -> "baron d'")
      var called = 0
      def httpClient: HttpClient[IO] = HttpClientMock.test[IO] {
        case ("https://www.vivino.com/search/wines", map) =>
          called += 1
          called shouldBe 1
          readFile("vivino_search_mock.html")
      }

      val expected: List[Wine] = List(
        Wine(s"Wine 1", Some(1), Some(s"€ 1.0")),
        Wine(s"Wine 2", Some(2), None),
        Wine(s"Wine 3", Some(3), None),
      )

      wineClient.getWinesByName("baron d'").unsafeRunSync() shouldBe expected
    }
    "adviseWine" in new Scope {
      val map: Map[String, String] = Map(
        "page" -> "1",
        "country_code" -> "fr",
        "country_codes[]" -> "fr",
        "order" -> "asc",
        "order_by" -> "price",
        "currency_code" -> "EUR",
        "wine_type_ids[]" -> "1",
      )
      var called = 0
      def httpClient: HttpClient[IO] = HttpClientMock.test[IO] {
        case ("https://www.vivino.com/api/explore/explore", map) =>
          called += 1
          called shouldBe 1
          readFile("vivino_explore_mock.json")
      }

      val expected: List[Wine] = List(
        Wine(s"Wine 1", Some(1), Some(s"€ 1.0")),
        Wine(s"Wine 2", Some(2), None),
        Wine(s"Wine 3", Some(3), None),
      )

      wineClient.adviseWine(
        Some(CountryCode.France),
        currencyCode = Euro,
        wineType = Some(WineType.Red),
        ratingMin = None,
        priceMin = None,
        priceMax = None
      ).unsafeRunSync() shouldBe expected
    }
  }
}
