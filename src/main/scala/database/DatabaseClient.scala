package database

import bot.domain.AdviseState
import cats.effect._
import database.models.{AdviseHistory, User}
import doobie._
import doobie.implicits._
import doobie.postgres.circe.jsonb.implicits._
import doobie.util.transactor.Transactor.Aux
import io.circe.generic.auto._

trait DatabaseClient[F[_]] {

}

class DoobieDatabaseClient[F[_] : Async] extends DatabaseClient[F] {
  private val transactor: Aux[F, Unit] = Transactor.fromDriverManager[F](
    "org.postgresql.Driver",
    "jdbc:postgresql://localhost:5432/cavisto",
    "cavisto",
    "cavisto"
  )

  implicit val metaAdviseState: Meta[AdviseState] = new Meta(pgDecoderGet, pgEncoderPut)

  def dropUsersTable(): F[Int] = sql"""DROP TABLE IF EXISTS users""".update.run.transact(transactor)

  def createUsersTable(): F[Int] =
    sql"""
    CREATE TABLE users (
      id   SERIAL PRIMARY KEY,
      telegram_id INTEGER NOT NULL UNIQUE,
      first_name VARCHAR NULL,
      last_name VARCHAR NULL,
      username VARCHAR NULL UNIQUE,
      created_at timestamp default now()
    )
  """.update.run.transact(transactor)

  def dropAdviseHistoryTable(): F[Int] = sql"""DROP TABLE IF EXISTS advise_history""".update.run.transact(transactor)

  def createAdviseHistoryTable(): F[Int] =
    sql"""
    CREATE TABLE advise_history (
      id   SERIAL PRIMARY KEY,
      user_id   INTEGER NOT NULL,
      advise_state jsonb not null default '{}'::jsonb,
      created_at timestamp default now(),
      FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
    )
  """.update.run.transact(transactor)

  def insertUser(telegram_id: Long, username: Option[String]): F[Int] =
    sql"insert into users (telegram_id, username) values ($telegram_id, $username)".update.run.transact(transactor)

  def selectUser(telegram_id: Long): F[Option[User]] =
    sql"select telegram_id, first_name, last_name, username, created_at from users where telegram_id = $telegram_id".query[User].option.transact(transactor)

  def insertAdviseHistory(user_id: Int, adviseState: AdviseState): F[Int] =
    sql"insert into advise_history (user_id, advise_state) values ($user_id, $adviseState)".update.run.transact(transactor)

  def getAdviseHistory(user_id: Long): F[List[AdviseHistory]] =
    sql"select user_id, advise_state, created_at from advise_history where user_id = $user_id".query[AdviseHistory].to[List].transact(transactor)

}
