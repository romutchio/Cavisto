package mocks

import cats.effect.Sync
import vivino.WineClient
import vivino.domain.{CountryCode, CurrencyCode, Wine, WineType}

class WineClientMock[F[_]: Sync] extends WineClient[F] {

  val winesMocked: List[Wine] = List(
    Wine(s"Wine 1", Some(1), Some(s"€ 1.0")),
    Wine(s"Wine 2", Some(2), None),
    Wine(s"Wine 3", Some(3), None),
  )
  def getWinesByName(name: String): F[List[Wine]] = Sync[F].pure(winesMocked)
  def adviseWine(
    countryCode: Option[CountryCode],
    currencyCode: CurrencyCode,
    wineType: Option[WineType],
    ratingMin: Option[Int],
    priceMin: Option[Int],
    priceMax: Option[Int]
  ): F[List[Wine]] = Sync[F].pure(winesMocked)
}
