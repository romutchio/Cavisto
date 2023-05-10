package wineClient

import cats.effect.unsafe.implicits.global
import cats.effect.{IO, Sync}
import client.HttpClient
import io.circe.Decoder
import io.circe.parser.decode
import models.ExploreResponse
import models.VivinoModels._
import org.scalamock.scalatest.MockFactory
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import parser.JsoupVivinoHtmlParser
import vivino.VivinoWineClient
import vivino.domain.CurrencyCode.Euro
import vivino.domain.{CountryCode, Wine, WineType}

import scala.io.Source

class WineClientTest extends AnyFreeSpec with Matchers with MockFactory {
  val fakeHttp: HttpClient[IO] = mock[HttpClient[IO]]
  val vivinoHtmlParser = new JsoupVivinoHtmlParser[IO]()
  val wineClient = new VivinoWineClient[IO](vivinoHtmlParser, fakeHttp)

  def readFile(filename: String): String = {
    val src = Source.fromResource(filename)
    src.mkString
  }

  "WineClient" - {
    "getWinesByName" in {
      (fakeHttp.get _)
        .expects(
          "https://www.vivino.com/search/wines",
          Map("q" -> "baron d'")
        )
        .once()
        .returns(Sync[IO].delay(readFile("vivino_search_mock.html")))
      val actual = wineClient.getWinesByName("baron d'").unsafeRunSync()

      val expected: List[Wine] = List(
        Wine(s"Wine 1", Some(1), Some(s"€ 1.0")),
        Wine(s"Wine 2", Some(2), None),
        Wine(s"Wine 3", Some(3), None),
      )

      actual shouldBe expected
    }
    "adviseWine" in {
      (fakeHttp.getJson[ExploreResponse](_: String, _: Map[String, String]) (_: Decoder[ExploreResponse])).expects(
          "https://www.vivino.com/api/explore/explore",
          Map(
            "page" -> "1",
            "country_code" -> "fr",
            "country_codes[]" -> "fr",
            "order" -> "asc",
            "order_by" -> "price",
            "currency_code" -> "EUR",
            "wine_type_ids[]" -> "1",
          ),
          *,
        ).once()
        .returns(
          Sync[IO].delay(decode[ExploreResponse](readFile("vivino_explore_mock.json")).toOption.get)
      )

      val actual = wineClient.adviseWine(
        Some(CountryCode.France),
        currencyCode = Euro,
        wineType = Some(WineType.Red),
        ratingMin = None,
        priceMin = None,
        priceMax = None
      ).unsafeRunSync()

      val expected: List[Wine] = List(
        Wine(s"Wine 1", Some(1), Some(s"€ 1.0")),
        Wine(s"Wine 2", Some(2), None),
        Wine(s"Wine 3", Some(3), None),
      )

      actual shouldBe expected
    }
  }
}
