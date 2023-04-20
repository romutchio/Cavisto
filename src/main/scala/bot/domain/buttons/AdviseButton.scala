package bot.domain.buttons

sealed abstract class AdviseButton extends Button {
  val tag: String = "Advise_TAG"
}

object AdviseButton {

  final case object Return extends AdviseButton {
    val emoji: Option[String] = Some("«")
    val name: String = "Back"
  }

  final case object Advise extends AdviseButton {
    val emoji: Option[String] = Some("✅")
    val name: String = "Advise"
  }
}
