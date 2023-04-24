package database.models

import java.util.Date

case class User(
  telegram_id: Long,
  first_name: Option[String],
  last_name: Option[String],
  username: Option[String],
  created_at: Date,
)


