package stateStore

import bot.StateStore
import bot.domain.states.{AdviseState, NoteState}
import cats.effect.IO
import cats.effect.unsafe.implicits.global
import com.bot4s.telegram.models.{CallbackQuery, Chat, ChatType, Message, User}
import mocks.StateStoreMock
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import vivino.domain.{CountryCode, WineType}

class StateStoreTest extends AnyFreeSpec with Matchers {

  "StateStore" - {
    "setMessageState" in {
      implicit val message: Message = Message(messageId = 1, date = 1, chat = Chat(id=1.toLong, `type` = ChatType.Channel))

      val initAdviseState: AdviseState = AdviseState(country = Some(CountryCode.Russia.code), wineType = Some(WineType.Rose.id.toString))
      val updatedAdviseState: AdviseState = AdviseState(Some(CountryCode.Germany.code), wineType = Some(WineType.Dessert.id.toString))
      val state: Map[Long, AdviseState] = Map(message.chat.id -> initAdviseState)
      val stateStore: StateStore[IO, AdviseState] = StateStoreMock.test[IO, AdviseState](state, AdviseState.empty).unsafeRunSync()

      stateStore.getMessageState.unsafeRunSync() shouldBe initAdviseState

      stateStore.setMessageState(updatedAdviseState).unsafeRunSync()

      stateStore.getMessageState.unsafeRunSync() shouldBe updatedAdviseState
    }

    "getMessageState" in {
      implicit val message: Message = Message(messageId = 1, date = 1, chat = Chat(id=1.toLong, `type` = ChatType.Channel))
      val adviseState = AdviseState(Some(CountryCode.UnitedStates.code))
      val state: Map[Long, AdviseState] = Map(1.toLong -> adviseState)
      val stateStore: StateStore[IO, AdviseState] = StateStoreMock.test[IO, AdviseState](state, AdviseState.empty).unsafeRunSync()

      stateStore.getMessageState.unsafeRunSync() shouldBe adviseState
    }

    "setCallbackState" in {
      val user = User(id = 1.toLong, isBot = false, firstName = "firstName")
      val message = Message(messageId = 1, date = 1, chat = Chat(id=1.toLong, `type` = ChatType.Channel))
      implicit val callbackQuery: CallbackQuery = CallbackQuery(id = "id", from = user, message = Some(message), chatInstance = "chatInstance")

      val initNoteState: NoteState = NoteState(wineName = Some("Some wine name"), rating = Some(3.4))
      val updatedNoteState: NoteState = NoteState(wineName = Some("Another wine name"), rating = Some(2.1))
      val state: Map[Long, NoteState] = Map(message.chat.id -> initNoteState)
      val stateStore: StateStore[IO, NoteState] = StateStoreMock.test[IO, NoteState](state, NoteState.empty).unsafeRunSync()

      stateStore.getCallbackState.unsafeRunSync() shouldBe initNoteState

      stateStore.setCallbackState(updatedNoteState).unsafeRunSync()

      stateStore.getCallbackState.unsafeRunSync() shouldBe updatedNoteState
    }

    "getCallbackState" in {
      implicit val message: Message = Message(messageId = 1, date = 1, chat = Chat(id = 1.toLong, `type` = ChatType.Channel))
      val noteState = NoteState(price = Some(40.2), review = Some("Short wine review"))
      val state: Map[Long, NoteState] = Map(1.toLong -> noteState)
      val stateStore: StateStore[IO, NoteState] = StateStoreMock.test[IO, NoteState](state, NoteState.empty).unsafeRunSync()

      stateStore.getMessageState.unsafeRunSync() shouldBe noteState
    }
  }
}
