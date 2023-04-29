package bot.domain.buttons

sealed abstract class NoteButton extends Button {
  val tag: String = "NOTE_TAG"
}

object EditNoteButton {
  final case object Name extends Button {
    val emoji: Option[String] = None
    val name: String = "Edit Wine name"
    val tag: String = "NOTE_EDIT_NAME_TAG"
  }

  final case object Rating extends Button {
    val emoji: Option[String] = None
    val name: String = "Edit Rating"
    val tag: String = "NOTE_EDIT_RATING_TAG"
  }

  final case object Price extends Button {
    val emoji: Option[String] = None
    val name: String = "Edit Price"
    val tag: String = "NOTE_EDIT_PRICE_TAG"
  }

  final case object Review extends Button {
    val emoji: Option[String] = None
    val name: String = "Edit Review"
    val tag: String = "NOTE_EDIT_REVIEW_TAG"
  }
}

object NoteButton {

  final case object Return extends NoteButton {
    val emoji: Option[String] = Some("«")
    val name: String = "Back to notes"
  }

  final case object Save extends NoteButton {
    val emoji: Option[String] = Some("✅")
    val name: String = "Save note"
  }

}
