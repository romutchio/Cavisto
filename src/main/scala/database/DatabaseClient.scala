package database

import bot.domain.states.AdviseState
import cats.effect._
import database.models.{AdviseHistory, User}
import doobie._
import doobie.implicits._
import doobie.postgres.circe.jsonb.implicits._
import doobie.util.transactor.Transactor.Aux
import io.circe.generic.auto._

trait DatabaseClient[F[_]] {
  def insertUser(telegram_id: Long, username: Option[String], firstName: Option[String], lastName: Option[String]): F[Either[String, Int]]

  def getUser(telegram_id: Long): F[Either[String, Option[User]]]

  def insertAdviseHistory(user_id: Long, adviseState: AdviseState): F[Either[String, Int]]

  def getAdviseHistory(user_id: Long): F[Either[String, List[AdviseHistory]]]

  def insertNote(user_id: Long, wine_name: String, rating: Option[Double], price: Option[Double], review: Option[String]): F[Either[String, Int]]
}

class DoobieDatabaseClient[F[_] : Async] extends DatabaseClient[F] {
  private val transactor: Aux[F, Unit] = Transactor.fromDriverManager[F](
    "org.postgresql.Driver",
    "jdbc:postgresql://localhost:5432/cavisto",
    "cavisto",
    "cavisto"
  )

  implicit val metaAdviseState: Meta[AdviseState] = new Meta(pgDecoderGet, pgEncoderPut)

  def insertUser(telegram_id: Long, username: Option[String], firstName: Option[String], lastName: Option[String]): F[Either[String, Int]] =
    sql"insert into users (telegram_id, username, first_name, last_name) values ($telegram_id, $username, $firstName, $lastName) on conflict (telegram_id) do update set first_name = EXCLUDED.first_name, last_name = EXCLUDED.last_name"
      .update.run.transact(transactor).attemptSomeSqlState {
      case _ => "Error"
    }

  def getUser(telegram_id: Long): F[Either[String, Option[User]]] =
    sql"select id, telegram_id, first_name, last_name, username, created_at from users where telegram_id = $telegram_id".query[User]
      .option.transact(transactor).attemptSomeSqlState {
      case _ => "Error"
    }

  def insertAdviseHistory(user_id: Long, adviseState: AdviseState): F[Either[String, Int]] =
    sql"insert into advise_history (user_id, advise_state) values ($user_id, $adviseState)"
      .update.run.transact(transactor).attemptSomeSqlState {
      case _ => "Error"
    }

  def getAdviseHistory(user_id: Long): F[Either[String, List[AdviseHistory]]] =
    sql"select user_id, advise_state, created_at from advise_history where user_id = $user_id"
      .query[AdviseHistory].to[List].transact(transactor).attemptSomeSqlState {
      case _ => "Error"
    }

  def insertNote(user_id: Long, wine_name: String, rating: Option[Double], price: Option[Double], review: Option[String]): F[Either[String, Int]] =
    sql"insert into notes (user_id, wine_name, rating, price, review) values ($user_id, $wine_name, $rating, $price, $review)"
      .update.run.transact(transactor).attemptSomeSqlState {
      case _ => "Error"
    }

}


object DoobieDatabaseClient {
  def make[F[_] : Async]: F[DoobieDatabaseClient[F]] = Async[F].delay(new DoobieDatabaseClient[F])
}