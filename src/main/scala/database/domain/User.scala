package database.domain

import java.util.Date

case class User(
  user_id: Long,
  telegram_id: Long,
  first_name: Option[String],
  last_name: Option[String],
  username: Option[String],
  created_at: Date,
)


