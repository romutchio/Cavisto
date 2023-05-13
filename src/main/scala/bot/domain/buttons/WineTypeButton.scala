package bot.domain.buttons

import bot.domain.Button
import enumeratum._
import vivino.domain.WineType

abstract class WineTypeButton extends Button {
  val tag: String = "WineType_TAG"
}

sealed abstract class Wine(val wineType: WineType)
  extends WineTypeButton with EnumEntry

object WineTypeButton extends Enum[Wine] {
  val values: IndexedSeq[Wine] = findValues

  final case object Selection extends WineTypeButton {
    val emoji: Option[String] = Some("\uD83C\uDF77")
    val name: String = "Type"
  }

  final case object Clear extends WineTypeButton {
    val emoji: Option[String] = None
    val name: String = "Clear"
  }

  final case object Red extends Wine(WineType.Red) {
    val emoji: Option[Nothing] = None
    val name = "Red"
  }

  final case object White extends Wine(WineType.White) {
    val emoji: Option[String] = None
    val name = "White"
  }

  final case object Sparkling extends Wine(WineType.Sparkling) {
    val emoji: Option[String] = None
    val name = "Sparkling"
  }

  final case object Rose extends Wine(WineType.Rose) {
    val emoji: Option[String] = None
    val name = "Rose"
  }

  final case object Dessert extends Wine(WineType.Dessert) {
    val emoji: Option[String] = None
    val name = "Dessert"
  }

  final case object Fortified extends Wine(WineType.Fortified) {
    val emoji: Option[String] = None
    val name = "Fortified"
  }
}
