package database.domain

case class Note(id: Long, user_id: Long, wine_name: Option[String], rating: Option[Double], price: Option[Double], review: Option[String])
