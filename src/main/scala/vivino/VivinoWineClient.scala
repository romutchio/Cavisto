package vivino

import cats.effect.Async
import cats.implicits._
import client.HttpClient
import io.circe.generic.auto._
import models.{ExploreResponse, Match}
import parser.VivinoHtmlParser
import vivino.domain.{CountryCode, CurrencyCode, Wine, WineType}


class VivinoWineClient[F[_] : Async](implicit vivinoHTMLParser: VivinoHtmlParser[F], httpClient: HttpClient[F]) extends WineClient[F] {
  def getWinesByName(name: String): F[List[Wine]] = {
    httpClient.get(url = "https://www.vivino.com/search/wines", query = Map("q" -> name))
      .flatMap(vivinoHTMLParser.parseSearchHtml)
  }

  private def wineFromMatch(m: Match): Wine = {
    val price = (m.price.currency.prefix, m.price.currency.suffix, m.price.amount) match {
      case (Some(prefix), None, value) => Some(s"$prefix $value")
      case (None, Some(suffix), value) => Some(s"$value $suffix")
      case _ => None
    }
    Wine(
      name = m.vintage.name,
      rating = m.vintage.statistics.wine_ratings_average,
      price = price
    )
  }

  def adviseWine(
    countryCode: Option[CountryCode],
    currencyCode: CurrencyCode,
    wineType: Option[WineType],
    ratingMin: Option[Int],
    priceMin: Option[Int],
    priceMax: Option[Int],
  ): F[List[Wine]]
  = {
    val queryStr: Map[String, String] =
      List(
        Some(Map("currency_code" -> currencyCode.code, "order_by" -> "price", "order" -> "asc")),
        countryCode.map(country => Map("country_code" -> country.code, "country_codes[]" -> country.code))
      ).flatten.reduce(_ |+| _)


    val queryInt: Map[String, Int] =
      List(
        Some(Map("page" -> 1)),
        ratingMin.map(rating => Map("min_rating" -> rating)),
        wineType.map(wine => Map("wine_type_ids[]" -> wine.id)),
        priceMin.map(price => Map("price_range_min" -> price)),
        priceMax.map(price => Map("price_range_max" -> price)),
      ).flatten.reduce(_ |+| _)

    httpClient.getJson[ExploreResponse](
      url = "https://www.vivino.com/api/explore/explore",
      queryStr = queryStr,
      queryInt = queryInt,
    ).flatMap(resp => Async[F].delay(resp.explore_vintage.matches.map(wineFromMatch)))
  }
}