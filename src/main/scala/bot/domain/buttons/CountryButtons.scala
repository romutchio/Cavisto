package bot.domain.buttons

object CountryButtons {

  abstract class CountryButton extends Button {
    val tag: String = "Country_TAG"
  }

  final case object CountrySelectionButton extends CountryButton {
    val emoji: Option[String] = Some("\uD83C\uDDEB\uD83C\uDDF7")
    val name: String = "Country"
  }

  final case object ClearCountrySelectionButton extends CountryButton {
    val emoji: Option[String] = None
    val name: String = "Clear"
  }


  final case object FranceCountryButton extends CountryButton {
    val emoji: Option[String] = Some("\uD83C\uDDEB\uD83C\uDDF7")
    val name: String = "France"
  }

  final case object ArgentinaCountryButton extends CountryButton {
    val emoji: Option[String] = Some("\uD83C\uDDE6\uD83C\uDDF7")
    val name: String = "Argentina"
  }

  final case object AustraliaCountryButton extends CountryButton {
    val emoji: Option[String] = Some("\uD83C\uDDE6\uD83C\uDDFA")
    val name: String = "Australia"
  }

  final case object  AustriaCountryButton extends CountryButton {
    val emoji: Option[String] = Some("\uD83C\uDDE6\uD83C\uDDF9")
    val name: String = "Austria"
  }

  final case object ChileCountryButton extends CountryButton {
    val emoji: Option[String] = Some("\uD83C\uDDE8\uD83C\uDDF1")
    val name: String = "Chile"
  }

  final case object GermanyCountryButton extends CountryButton {
    val emoji: Option[String] = Some("\uD83C\uDDE9\uD83C\uDDEA")
    val name: String = "Germany"
  }

  final case object ItalyCountryButton extends CountryButton {
    val emoji: Option[String] = Some("\uD83C\uDDEE\uD83C\uDDF9")
    val name: String = "Italy"
  }

  final case object PortugalCountryButton extends CountryButton {
    val emoji: Option[String] = Some("\uD83C\uDDF5\uD83C\uDDF9")
    val name: String = "Portugal"
  }

  final case object RussiaCountryButton extends CountryButton {
    val emoji: Option[String] = Some("\uD83C\uDDF7\uD83C\uDDFA")
    val name: String = "Russia"
  }

  final case object SpainCountryButton extends CountryButton {
    val emoji: Option[String] = Some("\uD83C\uDDEA\uD83C\uDDF8")
    val name: String = "Spain"
  }

  final case object UnitedStatesCountryButton extends CountryButton {
    val emoji: Option[String] = Some("\uD83C\uDDFA\uD83C\uDDF8")
    val name: String = "US"
  }

}
