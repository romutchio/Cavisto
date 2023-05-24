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
case class NotesListState(notes: List[Note] = Nil, pageNumber: Int = 0)

case class NoteState(
  wineName: Option[String] = None,
  rating: Option[Double] = None,
  price: Option[Double] = None,
  review: Option[String] = None,
  status: NoteEditStatus = Empty,
  notesListState: Option[NotesListState] = None,
  editMessageState: Option[EditMessageState] = None,
) extends State { self =>

  def validateWineName(wineName: Option[String]): Either[String, String] = for {
    wine <- wineName match {
      case Some(value) => if (value.length > 255)
        Left("Wine name is too long.")
      else
        Right(value)
      case None => Left("Wine name is empty.")
    }
  } yield wine

  def validateRating(rating: Option[Double]): Either[String, Double] = for {
    r <- rating match {
      case Some(value) => if (value < 1 || value > 5)
        Left("Rating should be between 1 and 5.")
      else
        Right(value)
      case None => Left("Rating should be number.")
    }
  } yield r

  def validatePrice(price: Option[Double]): Either[String, Double] = for {
    p <- price match {
      case Some(value) => if (value < 0)
        Left("Price should not be negative.")
      else
        Right(value)
      case None => Left("Price should be number.")
    }
  } yield p

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

  def updateNoteListState(notes: Option[List[Note]] = None, pageNumber: Option[Int] = None): NoteState = {
    val notesListState = self.notesListState.getOrElse(NotesListState.empty)
    val updatedState = notesListState.copy(
      notes = notes.getOrElse(notesListState.notes),
      pageNumber = pageNumber.getOrElse(notesListState.pageNumber),
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