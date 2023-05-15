package states

import bot.domain.states.{EditMessageState, NoteState, NotesListState}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import database.domain.Note

class NoteStateTest extends AnyFreeSpec with Matchers {

  "NoteState" - {
    "updateMessageState" - {
      "empty" in {
        val emptyState = NoteState.empty
        val source = Some(4.toLong)
        val messageId = Some(5)
        val noteDbId = Some(7.toLong)

        val expected = Some(EditMessageState(source, messageId, noteDbId))
        val updatedState = emptyState.updateMessageState(source, messageId, noteDbId)

        updatedState.editMessageState shouldBe expected
      }
      "partly filled" in {
        val stateBefore = Some(EditMessageState(Some(1.toLong)))
        val emptyState = NoteState.empty.copy(editMessageState = stateBefore)

        emptyState.editMessageState shouldBe stateBefore

        val source = Some(4.toLong)
        val messageId = Some(5)
        val noteDbId = Some(7.toLong)

        val expected = Some(EditMessageState(source, messageId, noteDbId))
        val updatedState = emptyState.updateMessageState(source, messageId, noteDbId)

        updatedState.editMessageState shouldBe expected
      }

    }

    "updateNoteListState" - {
      "empty" in {
        val emptyState = NoteState.empty

        emptyState.notesListState shouldBe None

        val notes = List(Note(1.toLong, 444.toLong), Note(2.toLong, 444.toLong))
        val pageNumber = 1

        val expected = Some(NotesListState(notes, pageNumber))
        val updatedState = emptyState.updateNoteListState(Some(notes), Some(pageNumber))

        updatedState.notesListState shouldBe expected
      }

      "update params not passed" in {
        val notes = List(Note(1.toLong, 444.toLong), Note(2.toLong, 444.toLong))
        val pageNumber = 1
        val stateBefore = Some(NotesListState(notes, pageNumber))
        val emptyState = NoteState.empty.copy(notesListState = stateBefore)

        emptyState.notesListState shouldBe stateBefore

        val updatedState = emptyState.updateNoteListState(None, None)

        updatedState.notesListState shouldBe stateBefore
      }

    }
  }
}