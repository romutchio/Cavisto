package parser

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import domain.Wine
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

import scala.io.Source

class JsoupParserTest extends AnyFreeSpec with Matchers {
  val parser = new JsoupVivinoHtmlParser[IO]()
  def getHtml(filename: String): String = {
    val src = Source.fromResource(filename)
    src.mkString
  }

  "JsoupVivinoHtmlParser" - {
    "parseSearchHtml" in {
      val html = getHtml(s"vivino_search.html")
      val expected = List(
        Wine("Baron d'Arignac Vin Rouge", Some(3.6), Some("570.0 ₽")),
        Wine("Baron Philippe de Rothschild Les Cépages Pinot Noir", Some(3.4), None),
        Wine("Baron Philippe de Rothschild Les Cépages Cabernet Sauvignon", Some(3.3), None)
      )
      val wines: List[Wine] = parser.parseSearchHtml(html).unsafeRunSync()
      wines shouldBe expected
    }
  }
}
