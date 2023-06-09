package vivino

import vivino.domain.{CountryCode, CurrencyCode, FoodPairing, GrapeType, Wine, WineType}

trait WineClient[F[_]] {
  def getWinesByName(name: String): F[List[Wine]]

  def adviseWine(
    countryCode: Option[CountryCode],
    currencyCode: CurrencyCode,
    wineType: Option[WineType],
    ratingMin: Option[Int],
    priceMin: Option[Int],
    priceMax: Option[Int],
    foodPairing: Option[FoodPairing],
    grapeType: Option[GrapeType]
  ): F[List[Wine]]
}