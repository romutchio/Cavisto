package states

import bot.domain.states.{AdviseState, WineListState}
import database.domain.Note
import vivino.domain.Wine
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers

class AdviseStateTest extends AnyFreeSpec with Matchers {

  "AdviseState" - {
    "updateWineListState" - {
      "empty" in {
        val emptyState = AdviseState.empty
        val wines = List(Wine("Wine 1"))

        val expected = Some(WineListState.empty)
        val updatedState = emptyState.updateWineListState(wines = Some(wines))

        updatedState.wineListState shouldBe expected
      }
    }
  }
}