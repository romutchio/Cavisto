package models

import io.circe.{Decoder, Encoder}
import io.circe.generic.auto._
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}

object VivinoModels {
  implicit val JsonEncoder: Encoder[ExploreResponse] = deriveEncoder
  implicit val JsonDecoder: Decoder[ExploreResponse] = deriveDecoder
}
case class Statistics(wine_ratings_average: Option[Double])
case class Vintage(id: BigInt, name: String, statistics: Statistics)
case class Currency(prefix: Option[String] = None, suffix: Option[String] = None)
case class Price(amount: Option[Double], currency: Currency)
case class Match(vintage: Vintage, price: Price)
case class ExploreVintage(matches: List[Match] = Nil)
case class ExploreResponse(explore_vintage: ExploreVintage)

