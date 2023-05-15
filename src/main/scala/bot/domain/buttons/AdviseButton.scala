package bot.domain.buttons

import bot.domain.Button

object AdviseButton {

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
}
