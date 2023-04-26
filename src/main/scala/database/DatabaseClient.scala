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
  def insertUser(telegram_id: Long, username: Option[String], firstName: Option[String], lastName: Option[String]): F[Int]
  def getUser(telegram_id: Long): F[Option[User]]
  def insertAdviseHistory(user_id: Long, adviseState: AdviseState): F[Int]
  def getAdviseHistory(user_id: Long): F[List[AdviseHistory]]
}

class DoobieDatabaseClient[F[_] : Async] extends DatabaseClient[F] {
  private val transactor: Aux[F, Unit] = Transactor.fromDriverManager[F](
    "org.postgresql.Driver",
    "jdbc:postgresql://localhost:5432/cavisto",
    "cavisto",
    "cavisto"
  )

  implicit val metaAdviseState: Meta[AdviseState] = new Meta(pgDecoderGet, pgEncoderPut)

  def insertUser(telegram_id: Long, username: Option[String], firstName: Option[String], lastName: Option[String]): F[Int] =
    sql"insert into users (telegram_id, username, first_name, last_name) values ($telegram_id, $username, $firstName, $lastName) on conflict (telegram_id) do update set first_name = EXCLUDED.first_name, last_name = EXCLUDED.last_name".update.run.transact(transactor)

  def getUser(telegram_id: Long): F[Option[User]] =
    sql"select id, telegram_id, first_name, last_name, username, created_at from users where telegram_id = $telegram_id".query[User].option.transact(transactor)

  def insertAdviseHistory(user_id: Long, adviseState: AdviseState): F[Int] =
    sql"insert into advise_history (user_id, advise_state) values ($user_id, $adviseState)".update.run.transact(transactor)

  def getAdviseHistory(user_id: Long): F[List[AdviseHistory]] =
    sql"select user_id, advise_state, created_at from advise_history where user_id = $user_id".query[AdviseHistory].to[List].transact(transactor)

}


object DoobieDatabaseClient {
  def make[F[_] : Async]: F[DoobieDatabaseClient[F]] = Async[F].delay(new DoobieDatabaseClient[F])
}