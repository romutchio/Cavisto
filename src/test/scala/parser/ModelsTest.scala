package parser

import org.scalatest.EitherValues
import io.circe.parser._
import io.circe.generic.auto._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import vivino.domain.ExploreResponse

import scala.io.Source

class ModelsTest extends AnyFreeSpec with Matchers with EitherValues {

  def fetchResponse(filename: String): Either[String, ExploreResponse] = {
    val src = Source.fromResource(filename)
    val s = src.mkString
    decode[ExploreResponse](s).left.map(_.getMessage)
  }

  "Vivino Json parsing" - {
    "matches empty" - {
        "get nil matches about wines" in {
        val response = fetchResponse(s"vivino_explore_nil_matches.json")
        val matches = response.map(_.explore_vintage.matches)
        matches.value shouldBe List.empty
      }
    }
    "matches full" - {
      "get full info about wines" in {
        val response = fetchResponse(s"vivino_explore_full.json")
        val matches = response.map(_.explore_vintage.matches.map(m => m.vintage.id))
        val expected = List(6803795, 156184948, 2184215, 3403779, 2367220, 100819098, 1405772, 147380568, 7043903, 1203058, 2619799, 1279099, 1195048, 1510217, 1229417, 2611979, 1225996, 1443891, 1889890, 1554088, 1697312, 93707, 2641965, 14207721, 2366894)
        matches.value shouldBe expected
      }
    }
  }
}