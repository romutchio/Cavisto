package bot.domain.states

import bot.domain.State

sealed trait NoteEditStatus

case object AwaitingWineNameEdit extends NoteEditStatus

case object AwaitingReviewEdit extends NoteEditStatus

case object AwaitingPriceEdit extends NoteEditStatus

case object AwaitingRatingEdit extends NoteEditStatus

case object Empty extends NoteEditStatus

case class NoteState(
  wineName: Option[String],
  rating: Option[Double],
  price: Option[Double],
  review: Option[String],
  status: NoteEditStatus,
  messageSource: Long,
  messageId: Int,
) extends State

object NoteState {
  def empty: NoteState = NoteState(None, None, None, None, Empty, 0, 0)
}