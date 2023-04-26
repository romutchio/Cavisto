package bot.domain

import bot.domain.buttons.{CountryButton, WineTypeButton}
import vivino.domain.{CountryCode, WineType}

case class AdviseState(
  country: Option[String],
  wineType: Option[String],
  priceMin: Option[Int],
  priceMax: Option[Int],
) {
  def getCountryCode: Option[CountryCode] = this.country.flatMap(CountryButton.withNameOption(_).map(_.countryCode))

  def getWineType: Option[WineType] = this.wineType.flatMap(WineTypeButton.withNameOption(_).map(_.wineType))
}

object AdviseState {
  def empty: AdviseState = AdviseState(None, None, None, None)
}
