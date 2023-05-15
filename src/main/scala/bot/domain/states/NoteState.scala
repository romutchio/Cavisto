package bot.domain.states

import bot.domain.State
import database.domain.Note

sealed trait NoteEditStatus

case object AwaitingWineNameEdit extends NoteEditStatus

case object AwaitingReviewEdit extends NoteEditStatus

case object AwaitingPriceEdit extends NoteEditStatus

case object AwaitingRatingEdit extends NoteEditStatus

case object Empty extends NoteEditStatus

case class EditMessageState(messageSource: Option[Long] = None, messageId: Option[Int] = None, noteDbId: Option[Long] = None)
case class NotesListState(notes: List[Note] = Nil, noteIdx: Int = 0)

case class NoteState(
  wineName: Option[String] = None,
  rating: Option[Double] = None,
  price: Option[Double] = None,
  review: Option[String] = None,
  status: NoteEditStatus = Empty,
  notesListState: Option[NotesListState] = None,
  editMessageState: Option[EditMessageState] = None,
) extends State { self =>
  def updateMessageState(messageSource: Option[Long] = None, messageId: Option[Int] = None, noteDbId: Option[Long] = None): NoteState = {
    val editMessageState = self.editMessageState.getOrElse(EditMessageState.empty)
    val updatedState = editMessageState.copy(
      messageSource = messageSource.orElse(editMessageState.messageSource),
      messageId = messageId.orElse(editMessageState.messageId),
      noteDbId = noteDbId.orElse(editMessageState.noteDbId),
    )
    self.copy(
      editMessageState = Some(updatedState)
    )
  }

  def updateNoteListState(notes: Option[List[Note]] = None, noteIdx: Option[Int] = None): NoteState = {
    val notesListState = self.notesListState.getOrElse(NotesListState.empty)
    val updatedState = notesListState.copy(
      notes = notes.getOrElse(notesListState.notes),
      noteIdx = noteIdx.getOrElse(notesListState.noteIdx),
    )
    self.copy(
      notesListState = Some(updatedState),
    )
  }

  def fromNote(note: Note): NoteState = {
    self.copy(
      wineName = note.wine_name,
      rating = note.rating,
      price = note.price,
      review = note.review,
      status = Empty,
    )
  }
}

object NoteState {
  def empty: NoteState = NoteState(None, None, None, None, Empty)
}

object EditMessageState {
  def empty: EditMessageState = EditMessageState()
}

object NotesListState {
  def empty: NotesListState = NotesListState()
}