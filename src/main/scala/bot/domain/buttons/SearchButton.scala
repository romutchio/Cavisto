package bot.domain.buttons

import bot.domain.Button

object SearchButton {
  val ControlButtonsTag: String = "SearchControl_TAG"

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
}

