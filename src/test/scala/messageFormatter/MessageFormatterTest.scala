package messageFormatter

import bot.MessageFormatter
import bot.domain.buttons.{CountryButton, WineTypeButton}
import bot.domain.states.AdviseState
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import vivino.domain.Wine

class MessageFormatterTest extends AnyFreeSpec with Matchers {
  val messageFormatter = new MessageFormatter()

  "MessageFormatter" - {
    "getWineMessage" - {
      "single" in {
        val wines = List(Wine("Wine 1"))
        val expected = "*Wine 1* _Price: ..._ _Rating: ..._"
        messageFormatter.getWinesFoundMessage(wines) shouldBe expected
      }

      "multiple" in {
        val wines = List(
          Wine("Wine 1", rating = Some(3.2), price = Some("42.12")),
          Wine("Wine 2", rating = None, price = Some("12.23")),
          Wine("Wine 3", rating = Some(4.7), None),
        )
        val expected =
          """*Wine 1* _Price: 42.12_ _Rating: 3.2_
            |*Wine 2* _Price: 12.23_ _Rating: ..._
            |*Wine 3* _Price: ..._ _Rating: 4.7_""".stripMargin
        messageFormatter.getWinesFoundMessage(wines) shouldBe expected
      }
    }

    "adviseStateView" - {
      "empty" in {
        val expected =
          """*Country:* ...
            |*Type:* ...
            |*Price from:* ...
            |*Price to:* ...""".stripMargin

        messageFormatter.adviseStateView(AdviseState.empty) shouldBe expected
      }

      "filled" in {
        val expected =
          """*Country:* Spain
            |*Type:* White
            |*Price from:* € 40
            |*Price to:* € 90""".stripMargin

        val adviseState = AdviseState(Some(CountryButton.Spain.name), Some(WineTypeButton.White.name), Some(40), Some(90))
        messageFormatter.adviseStateView(adviseState) shouldBe expected
      }
    }
  }
}
