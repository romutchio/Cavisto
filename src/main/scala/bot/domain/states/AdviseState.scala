package bot.domain.states

import bot.domain.State
import bot.domain.buttons.{CountryButton, WineTypeButton}
import vivino.domain.{CountryCode, WineType}

case class AdviseState(
  country: Option[String],
  wineType: Option[String],
  priceMin: Option[Int],
  priceMax: Option[Int],
) extends State { self =>
  def getCountryCode: Option[CountryCode] = self.country.flatMap(CountryButton.withNameOption(_).map(_.countryCode))

  def getWineType: Option[WineType] = self.wineType.flatMap(WineTypeButton.withNameOption(_).map(_.wineType))
}

object AdviseState {
  def empty: AdviseState = AdviseState(None, None, None, None)
}