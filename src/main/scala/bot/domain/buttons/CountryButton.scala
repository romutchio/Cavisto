package bot.domain.buttons

import bot.domain.Button
import enumeratum._
import vivino.domain.CountryCode

sealed abstract class CountryButton extends Button {
  val tag: String = "Country_TAG"
}

sealed abstract class Country(val countryCode: CountryCode)
  extends CountryButton with EnumEntry

object CountryButton extends Enum[Country] {
  val values: IndexedSeq[Country] = findValues

  final case object Selection extends CountryButton {
    val emoji: Option[String] = Some("\uD83C\uDDEB\uD83C\uDDF7")
    val name: String = "Country"
  }

  final case object Clear extends CountryButton {
    val emoji: Option[String] = None
    val name: String = "Clear"
  }

  case object France extends Country(CountryCode.France) {
    val emoji: Option[String] = Some("\uD83C\uDDEB\uD83C\uDDF7")
    val name: String = "France"
  }

  case object Argentina extends Country(CountryCode.Argentina) {
    val emoji: Option[String] = Some("\uD83C\uDDE6\uD83C\uDDF7")
    val name: String = "Argentina"
  }

  case object Australia extends Country(CountryCode.Australia) {
    val emoji: Option[String] = Some("\uD83C\uDDE6\uD83C\uDDFA")
    val name: String = "Australia"
  }

  case object Austria extends Country(CountryCode.Austria) {
    val emoji: Option[String] = Some("\uD83C\uDDE6\uD83C\uDDF9")
    val name: String = "Austria"
  }

  case object Chile extends Country(CountryCode.Chile) {
    val emoji: Option[String] = Some("\uD83C\uDDE8\uD83C\uDDF1")
    val name: String = "Chile"
  }

  case object Germany extends Country(CountryCode.Germany) {
    val emoji: Option[String] = Some("\uD83C\uDDE9\uD83C\uDDEA")
    val name: String = "Germany"
  }

  case object Italy extends Country(CountryCode.Italy) {
    val emoji: Option[String] = Some("\uD83C\uDDEE\uD83C\uDDF9")
    val name: String = "Italy"
  }

  case object Portugal extends Country(CountryCode.Portugal) {
    val emoji: Option[String] = Some("\uD83C\uDDF5\uD83C\uDDF9")
    val name: String = "Portugal"
  }

  case object Russia extends Country(CountryCode.Russia) {
    val emoji: Option[String] = Some("\uD83C\uDDF7\uD83C\uDDFA")
    val name: String = "Russia"
  }

  case object Spain extends Country(CountryCode.Spain) {
    val emoji: Option[String] = Some("\uD83C\uDDEA\uD83C\uDDF8")
    val name: String = "Spain"
  }

  case object UnitedStates extends Country(CountryCode.UnitedStates) {
    val emoji: Option[String] = Some("\uD83C\uDDFA\uD83C\uDDF8")
    val name: String = "US"
  }
}
