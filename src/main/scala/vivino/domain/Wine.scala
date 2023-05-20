package vivino.domain

case class Wine(name: String, rating: Option[Double] = None, price: Option[String] = None, url: Option[String] = None)
