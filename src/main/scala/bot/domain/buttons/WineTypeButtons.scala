package bot.domain.buttons

object WineTypeButtons {
  abstract class WineTypeButton extends Button {
    val tag: String = "WineType_TAG"
  }

  final case object WineTypeSelectionButton extends WineTypeButton {
    val emoji: Option[String] = Some("\uD83C\uDF77")
    val name: String = "Type"
  }

  final case object ClearWineTypeSelectionButton extends WineTypeButton {
    val emoji: Option[String] = None
    val name: String = "Clear"
  }

  final case object RedWineButton extends WineTypeButton {
    val emoji: Option[Nothing] = None
    val name = "Red"
  }

  final case object WhiteWineButton extends WineTypeButton {
    val emoji: Option[String] = None
    val name = "White"
  }

  final case object SparklingWineButton extends WineTypeButton {
    val emoji: Option[String] = None
    val name = "Sparkling"
  }

  final case object RoseWineButton extends WineTypeButton {
    val emoji: Option[String] = None
    val name = "Rose"
  }

  final case object DessertWineButton extends WineTypeButton {
    val emoji: Option[String] = None
    val name = "Dessert"
  }

  final case object FortifiedWineButton extends WineTypeButton {
    val emoji: Option[String] = None
    val name = "Fortified"
  }
}
