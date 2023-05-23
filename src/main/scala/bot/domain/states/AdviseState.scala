package bot.domain.states

import bot.domain.State
import bot.domain.buttons.{CountryButton, FoodPairingButton, WineTypeButton}
import vivino.domain.{CountryCode, FoodPairing, Wine, WineType}

case class WineListState(wines: List[Wine] = Nil, wineId: Int = 0) extends State

case class AdviseState(
  country: Option[String] = None,
  wineType: Option[String] = None,
  priceMin: Option[Int] = None,
  priceMax: Option[Int] = None,
  foodPairing: Option[String] = None,
  wineListState: Option[WineListState] = None,
) extends State { self =>

  def updateWineListState(wines: Option[List[Wine]] = None, wineId: Option[Int] = None): AdviseState = {
    val wineListState = self.wineListState.getOrElse(WineListState.empty)
    val updatedState = wineListState.copy(
      wines = wines.getOrElse(wineListState.wines),
      wineId = wineId.getOrElse(wineListState.wineId),
    )
    self.copy(
      wineListState = Some(updatedState),
    )
  }
  def getCountryCode: Option[CountryCode] = self.country.flatMap(CountryButton.withNameOption(_).map(_.countryCode))

  def getWineType: Option[WineType] = self.wineType.flatMap(WineTypeButton.withNameOption(_).map(_.wineType))

  def getFoodPairing: Option[FoodPairing] = self.foodPairing.flatMap(FoodPairingButton.withNameOption(_).map(_.foodPairing))
}

object AdviseState {
  def empty: AdviseState = AdviseState(None, None, None, None)
}

object WineListState {
  def empty: WineListState = WineListState()
}