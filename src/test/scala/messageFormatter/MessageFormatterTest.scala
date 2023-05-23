package messageFormatter

import bot.MessageFormatter
import bot.domain.buttons.{CountryButton, FoodPairingButton, GrapeTypeButton, WineTypeButton}
import bot.domain.states.AdviseState
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import vivino.domain.{FoodPairing, Wine}

class MessageFormatterTest extends AnyFreeSpec with Matchers {
  val messageFormatter = new MessageFormatter()

  "MessageFormatter" - {
    "getWineMessage" - {
      "only name" in {
        val wine = Wine("Wine 1")
        val expected =
          """Found 1 wines:
            |
            |0. *Wine 1*
            |_Price: ..._
            |_Rating: ..._""".stripMargin
        messageFormatter.getWineMessage(wine, 0, 1) shouldBe expected
      }

      "full" in {
        val wine = Wine("Wine 1", rating = Some(3.2), price = Some("42.12"), url = Some("https://some/url.png"))
        val expected =
          """Found 1 wines:
            |[​](https://some/url.png)
            |0. *Wine 1*
            |_Price: 42.12_
            |_Rating: 3.2_""".stripMargin
        messageFormatter.getWineMessage(wine, 0, 1) shouldBe expected
      }
    }

    "adviseStateView" - {
      "empty" in {
        val expected =
          """*Country:* ...
            |*Type:* ...
            |*Food pairing:* ...
            |*Grape:* ...
            |*Price from:* ...
            |*Price to:* ...""".stripMargin

        messageFormatter.adviseStateView(AdviseState.empty) shouldBe expected
      }

      "filled" in {
        val expected =
          """*Country:* Spain
            |*Type:* White
            |*Food pairing:* Pasta
            |*Grape:* Shiraz
            |*Price from:* € 40
            |*Price to:* € 90""".stripMargin

        val adviseState = AdviseState(
          Some(CountryButton.Spain.name),
          Some(WineTypeButton.White.name),
          Some(40),
          Some(90),
          Some(FoodPairingButton.Pasta.name),
          Some(GrapeTypeButton.Shiraz.name),
        )
        messageFormatter.adviseStateView(adviseState) shouldBe expected
      }
    }
  }
}
