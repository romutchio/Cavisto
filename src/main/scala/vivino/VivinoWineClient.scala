package vivino

import cats.effect.Async
import cats.implicits._
import client.HttpClient
import domain.{CountryCode, CurrencyCode, Wine}
import models.{ExploreResponse, Match}
import io.circe.generic.auto._
import parser.VivinoHtmlParser


class VivinoWineClient[F[_]: Async](implicit vivinoHTMLParser: VivinoHtmlParser[F], httpClient: HttpClient[F]) extends WineClient[F] {
  def getWinesByName(name: String): F[List[Wine]] = {
    httpClient.get(url= "https://www.vivino.com/search/wines", query = Map("q" -> name))
      .flatMap(vivinoHTMLParser.parseSearchHtml)
  }

  private def winesFromMatches(matches: List[Match]): F[List[Wine]] = Async[F].delay {
    matches.map(wineFromMatches)
  }
  private def wineFromMatches(m: Match): Wine = {
    val price = (m.price.amount, m.price.currency.prefix) match {
        case (amount, prefix) => Some(s"$amount $prefix")
        case _ => None
    }
    Wine(
      name = m.vintage.name,
      rating = m.vintage.statistics.wine_ratings_average,
      price = price
    )
  }

  def adviseWine(countryCode: CountryCode, currencyCode: CurrencyCode, ratingMin: Int, ratingMax: Int): F[List[Wine]]
  = {
    httpClient.getJson[ExploreResponse](
      url = "https://www.vivino.com/api/explore/explore",
      queryStr = Map(
        "country_code" -> countryCode.code,
        "currency_code" -> currencyCode.code,
      ),
      queryInt = Map(
        "min_rating" -> ratingMin,
        "max_rating" -> ratingMax,
        "page" -> 1,
      )
    ).flatMap(resp => winesFromMatches(resp.explore_vintage.matches))
  }
}