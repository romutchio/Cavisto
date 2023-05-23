package bot.domain.buttons

import bot.domain.Button
import enumeratum._
import vivino.domain.FoodPairing

sealed abstract class FoodPairingButton extends Button {
  val tag: String = "FoodPairing_TAG"
}

sealed abstract class Food(val foodPairing: FoodPairing)
  extends FoodPairingButton with EnumEntry

object FoodPairingButton extends Enum[Food] {
  val values: IndexedSeq[Food] = findValues

  final case object Selection extends FoodPairingButton {
    val emoji: Option[String] = Some("\uD83C\uDF7D")
    val name: String = "Food pairing"
  }

  final case object Clear extends FoodPairingButton {
    val emoji: Option[String] = None
    val name: String = "Clear"
  }

  case object Aperitif extends Food(FoodPairing.Aperitif) {
    val emoji: Option[String] = Some("\uD83C\uDF78")
    val name: String = "Aperitif"
  }

  case object Appetizers extends Food(FoodPairing.AppetizersSnacks) {
    val emoji: Option[String] = Some("\uD83C\uDF61")
    val name: String = "Appetizers"
  }

  case object AnyJunkFood extends Food(FoodPairing.AnyJunkFood) {
    val emoji: Option[String] = Some("\uD83C\uDF54")
    val name: String = "Any junk food"
  }
  case object Beef extends Food(FoodPairing.Beef) {
    val emoji: Option[String] = Some("\uD83E\uDD69")
    val name: String = "Beef"
  }

  case object CuredMeat extends Food(FoodPairing.CuredMeat) {
    val emoji: Option[String] = Some("\uD83E\uDD53")
    val name: String = "Cured meat"
  }

  case object Pork extends Food(FoodPairing.Pork) {
    val emoji: Option[String] = Some("\uD83D\uDC37")
    val name: String = "Pork"
  }

  case object Poultry extends Food(FoodPairing.Poultry) {
    val emoji: Option[String] = Some("\uD83C\uDF57")
    val name: String = "Poultry"
  }

  case object Shellfish extends Food(FoodPairing.Shellfish) {
    val emoji: Option[String] = Some("\uD83E\uDD90")
    val name: String = "Shellfish"
  }

  case object LeanFish extends Food(FoodPairing.LeanFish) {
    val emoji: Option[String] = Some("\uD83D\uDC1F")
    val name: String = "Lean fish"
  }

  case object RichFish extends Food(FoodPairing.RichFish) {
    val emoji: Option[String] = Some("\uD83D\uDC20")
    val name: String = "Rich fish"
  }

  case object FruityDesserts extends Food(FoodPairing.FruityDesserts) {
    val emoji: Option[String] = Some("\uD83C\uDF52")
    val name: String = "Fruity desserts"
  }

  case object SweetDesserts extends Food(FoodPairing.SweetDesserts) {
    val emoji: Option[String] = Some("\uD83C\uDF70")
    val name: String = "Sweet desserts"
  }


  case object MildAndSoftCheese extends Food(FoodPairing.MildAndSoftCheese) {
    val emoji: Option[String] = Some("\uD83E\uDDC0")
    val name: String = "Mild & Soft cheese"
  }

  case object MatureAndHardCheese extends Food(FoodPairing.MatureAndHardCheese) {
    val emoji: Option[String] = Some("\uD83E\uDDC0")
    val name: String = "Mature & Hard cheese"
  }

  case object Pasta extends Food(FoodPairing.Pasta) {
    val emoji: Option[String] = Some("\uD83C\uDF5D")
    val name: String = "Pasta"
  }

  case object SpicyFood extends Food(FoodPairing.SpicyFood) {
    val emoji: Option[String] = Some("\uD83C\uDF36")
    val name: String = "Spicy food"
  }

  case object Mushrooms extends Food(FoodPairing.Mushrooms) {
    val emoji: Option[String] = Some("\uD83C\uDF44")
    val name: String = "Mushrooms"
  }

  case object Vegetarian extends Food(FoodPairing.Vegetarian) {
    val emoji: Option[String] = Some("\uD83E\uDD66")
    val name: String = "Vegetarian"
  }

}
