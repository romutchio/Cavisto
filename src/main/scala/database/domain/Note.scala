package database.domain

case class Note(id: Long, user_id: Long, wine_name: Option[String] = None, rating: Option[Double] = None, price: Option[Double] = None, review: Option[String] = None)
