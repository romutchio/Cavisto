package database

import bot.domain.states.AdviseState
import cats.effect.Async
import database.domain.{AdviseHistory, Note, User}
import doobie.implicits._
import doobie.postgres.circe.jsonb.implicits.{pgDecoderGet, pgEncoderPut}
import doobie.util.transactor.Transactor.Aux
import doobie.{Meta, Transactor}
import io.circe.generic.auto._

class DoobieDatabaseClient[F[_] : Async](
  host: String, port: String, user: String, password: String, database: String
) extends DatabaseClient[F] {
  private val transactor: Aux[F, Unit] = Transactor.fromDriverManager[F](
    "org.postgresql.Driver",
    s"jdbc:postgresql://$host:$port/$database",
    user,
    password
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

  def getNotes(user_id: Long): F[Either[String, List[Note]]] =
    sql"select id, user_id, wine_name, rating, price, review from notes where user_id = $user_id order by created_at"
      .query[Note].to[List].transact(transactor).attemptSomeSqlState {
      case _ => "Error"
    }

  def updateNote(id: Long, wine_name: String, rating: Option[Double], price: Option[Double], review: Option[String]): F[Either[String, Int]] =
    sql"update notes set wine_name = $wine_name, rating = $rating, price = $price, review = $review where id = $id"
      .update.run.transact(transactor).attemptSomeSqlState {
      case _ => "Error"
    }
}

object DoobieDatabaseClient {
  def make[F[_] : Async](
    host: String, port: String, user: String, password: String, database: String
  ): F[DoobieDatabaseClient[F]] = Async[F].delay(new DoobieDatabaseClient[F](host, port, user, password, database))
}