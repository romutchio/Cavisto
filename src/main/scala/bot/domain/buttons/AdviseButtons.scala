package bot.domain.buttons

object AdviseButtons {
  abstract class AdviseButton extends Button {
    val tag: String = "Advise_TAG"
  }

  final case object ReturnToSelectionButton extends AdviseButton {
    val emoji: Option[String] = Some("«")
    val name: String = "Back"
  }

  final case object AdviseWineButton extends AdviseButton {
    val emoji: Option[String] = Some("✅")
    val name: String = "Advise"
  }
}
