package bot.domain.states

import bot.domain.State

sealed trait NoteEditStatus

case object AwaitingWineNameEdit extends NoteEditStatus

case object AwaitingReviewEdit extends NoteEditStatus

case object AwaitingPriceEdit extends NoteEditStatus

case object AwaitingRatingEdit extends NoteEditStatus

case object Empty extends NoteEditStatus

case class NoteState(
  wineName: Option[String] = None,
  rating: Option[Double] = None,
  price: Option[Double] = None,
  review: Option[String] = None,
  status: NoteEditStatus = Empty,
  messageSource: Long = 0,
  messageId: Int = 0,
) extends State

object NoteState {
  def empty: NoteState = NoteState(None, None, None, None, Empty)
}