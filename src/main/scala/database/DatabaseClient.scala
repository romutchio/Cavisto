package database

import bot.domain.states.AdviseState
import database.domain.{AdviseHistory, User}

trait DatabaseClient[F[_]] {
  def insertUser(telegram_id: Long, username: Option[String], firstName: Option[String], lastName: Option[String]): F[Either[String, Int]]

  def getUser(telegram_id: Long): F[Either[String, Option[User]]]

  def insertAdviseHistory(user_id: Long, adviseState: AdviseState): F[Either[String, Int]]

  def getAdviseHistory(user_id: Long): F[Either[String, List[AdviseHistory]]]

  def insertNote(user_id: Long, wine_name: String, rating: Option[Double], price: Option[Double], review: Option[String]): F[Either[String, Int]]
}
