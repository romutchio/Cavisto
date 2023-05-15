package bot.domain.buttons

import bot.domain.Button

object NotesButtons { self =>
  val ControlButtonsTag: String = "Notes_TAG"
  val SelectNotesTag: String = "NotesSelect_TAG"
  final case object Previous extends Button {
    val emoji: Option[String] = Some("◀\uFE0F")
    val name: String = "Previous"
    val tag: String = ControlButtonsTag
  }

  final case object Next extends Button {
    val emoji: Option[String] = Some("▶\uFE0F")
    val name: String = "Next"
    val tag: String = ControlButtonsTag
  }

  final case class NoteId(id: Int) extends Button {
    val name: String = id.toString
    val emoji: Option[String] = None
    val tag: String = SelectNotesTag
  }
}