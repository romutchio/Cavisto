package bot.domain.buttons

object PriceButtons {
  class PriceButtonType(val name: String, val tag: String)

  object PriceButtonType {
    final case object Min extends PriceButtonType(name = "Price from", tag = "PriceMin_TAG")

    final case object Max extends PriceButtonType(name = "Price to", tag = "PriceMax_TAG")
  }

  final case class PriceSelectionButton(priceButtonType: PriceButtonType) extends Button {
    val emoji: Option[String] = Some("\uD83D\uDCB6")
    val name: String = priceButtonType.name
    val tag: String = priceButtonType.tag
  }

  final case class EuroPriceButton(override val name: String, priceButtonType: PriceButtonType) extends Button {
    val emoji: Option[String] = Some("€")
    val tag: String = priceButtonType.tag
  }

  final case class ClearPriceSelectionButton(priceButtonType: PriceButtonType) extends Button {
    val emoji: Option[String] = None
    val name: String = "Clear"
    val tag: String = priceButtonType.tag
  }
}
