package bot.domain.buttons

import bot.domain.Button

object AdviseButton {
  val ControlButtonsTag: String = "Notes_TAG"

  final case object Return extends Button {
    val tag: String = "AdviseReturn_TAG"
    val emoji: Option[String] = Some("«")
    val name: String = "Back"
  }

  final case object Clear extends Button {
    val tag: String = "AdviseClear_TAG"
    val emoji: Option[String] = None
    val name: String = "Clear"
  }

  final case object Advise extends Button {
    val tag: String = "AdviseWine_TAG"
    val emoji: Option[String] = Some("✅")
    val name: String = "Advise"
  }

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
