package models

import io.circe.generic.auto._
import io.circe.parser.decode
case class Statistics(wine_ratings_average: Option[Double])
case class Vintage(id: BigInt, name: String, statistics: Statistics)
case class Currency(prefix: String)
case class Price(amount: Float, currency: Currency)
case class Match(vintage: Vintage, price: Price)
case class ExploreVintage(matches: List[Match] = Nil)
case class ExploreResponse(explore_vintage: ExploreVintage)

