package bot

import bot.domain.AdviseState
import cats.effect.{Ref, Sync}
import cats.implicits._
import com.bot4s.telegram.models.{CallbackQuery, Message}


class AdviseStateStore[F[_]: Sync](store: Ref[F, Map[Long, AdviseState]]) {
  def setCallbackState(value: AdviseState)(implicit cbq: CallbackQuery): F[Unit] = {
    for {
      _ <- store.update(_ + (cbq.message.get.chat.id -> value))
    } yield ()
  }

  def withMessageState(f: F[AdviseState] => F[Unit])(implicit msg: Message): F[Unit] = f(getMessageState)

  def withCallbackState(f: F[AdviseState] => F[Unit])(implicit cbq: CallbackQuery): F[Unit] = f(getCallbackState)


  def getMessageState(implicit msg: Message): F[AdviseState] = {
    for {
      s <- store.get
      adviseState = s.getOrElse(msg.chat.id, AdviseState.empty)
    } yield adviseState
  }

  def getCallbackState(implicit cbq: CallbackQuery): F[AdviseState] = {
    for {
      s <- store.get
      adviseState = s.getOrElse(cbq.message.get.chat.id, AdviseState.empty)
    } yield adviseState
  }

}

object AdviseStateStore {
  def make[F[_] : Sync]: F[AdviseStateStore[F]] = for {
    ref <- Ref.of[F, Map[Long, AdviseState]](Map.empty)
  } yield new AdviseStateStore[F](ref)
}