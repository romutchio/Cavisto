package bot.domain.buttons

import bot.domain.Button


sealed class PriceButtonType(val name: String, val tag: String)

object PriceButtonType {
  final case object Min extends PriceButtonType(name = "Price from", tag = "PriceMin_TAG")

  final case object Max extends PriceButtonType(name = "Price to", tag = "PriceMax_TAG")
}

sealed abstract class PriceButton(priceButtonType: PriceButtonType) extends Button {
  val tag: String = priceButtonType.tag
}

object PriceButton {
  final case class Selection(priceButtonType: PriceButtonType) extends PriceButton(priceButtonType) {
    val emoji: Option[String] = Some("\uD83D\uDCB6")
    val name: String = priceButtonType.name
  }

  final case class Euro(amount: Int, priceButtonType: PriceButtonType) extends PriceButton(priceButtonType) {
    val name: String = amount.toString
    val emoji: Option[String] = Some("€")
  }

  final case class Clear(priceButtonType: PriceButtonType) extends PriceButton(priceButtonType) {
    val emoji: Option[String] = None
    val name: String = "Clear"
  }

}
