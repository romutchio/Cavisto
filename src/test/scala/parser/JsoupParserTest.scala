package parser

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import vivino.domain.Wine
import vivino.parser.JsoupVivinoHtmlParser

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
        Wine("Baron d'Arignac Vin Rouge", Some(3.6), Some("570.0 ₽"), Some("images.vivino.com/thumbs/hS-d4fk0QXqXtcarkza-gQ_pb_300x300.png")),
        Wine("Baron Philippe de Rothschild Les Cépages Pinot Noir", Some(3.4), None, Some("images.vivino.com/thumbs/rz0KVQ7TSiqhqSqKGae3rw_pb_300x300.png")),
        Wine("Baron Philippe de Rothschild Les Cépages Cabernet Sauvignon", Some(3.3), None, Some("images.vivino.com/thumbs/out50fmkTjmxOLfuDviwMw_pb_300x300.png"))
      )
      val wines: List[Wine] = parser.parseSearchHtml(html).unsafeRunSync()
      wines shouldBe expected
    }
  }
}
