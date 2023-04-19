package bot.domain

import bot.domain.buttons.CountryButtons._
import bot.domain.buttons.WineTypeButtons._
import vivino.domain.CountryCode._
import vivino.domain.WineType._
import vivino.domain.{CountryCode, WineType}

case class AdviseState(
  country: Option[String],
  wineType: Option[String],
  priceMin: Option[Int],
  priceMax: Option[Int],
) {
  def toCountryCode: Option[CountryCode] = this.country.flatMap(AdviseState.CountryButtonToCountryCode.get)
  def toWineType: Option[WineType] = this.wineType.flatMap(AdviseState.WineTypeButtonToWineType.get)
}

object AdviseState {
  private val CountryButtonToCountryCode: Map[String, CountryCode] = Map(
    (FranceCountryButton.name,       France),
    (ArgentinaCountryButton.name,    Argentina),
    (AustraliaCountryButton.name,    Australia),
    (AustriaCountryButton.name,      Austria),
    (ChileCountryButton.name,        Chile),
    (GermanyCountryButton.name,      Germany),
    (ItalyCountryButton.name,        Italy),
    (PortugalCountryButton.name,     Portugal),
    (RussiaCountryButton.name,       Russia),
    (SpainCountryButton.name,        Spain),
    (UnitedStatesCountryButton.name, UnitedStates),
  )

  private val WineTypeButtonToWineType: Map[String, WineType] = Map(
    (RedWineButton.name,        Red),
    (WhiteWineButton.name,      White),
    (SparklingWineButton.name,  Sparkling),
    (RoseWineButton.name,       Rose),
    (DessertWineButton.name,    Dessert),
    (FortifiedWineButton.name,  Fortified),
  )
}