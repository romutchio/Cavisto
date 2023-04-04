package vivino

import domain.{CountryCode, CurrencyCode, Wine}

trait WineClient[F[_]] {
  def getWinesByName(name: String): F[List[Wine]]
  def adviseWine(countryCode: CountryCode, currencyCode: CurrencyCode, ratingMin: Int, ratingMax: Int): F[List[Wine]]
}