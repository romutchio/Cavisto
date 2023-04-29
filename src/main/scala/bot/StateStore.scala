package bot

import bot.domain.states.State
import cats.effect.{Ref, Sync}
import cats.implicits._
import com.bot4s.telegram.models.{CallbackQuery, Message}


class StateStore[F[_] : Sync, S <: State](store: Ref[F, Map[Long, S]], default: => S) {
  def setCallbackState(value: S)(implicit cbq: CallbackQuery): F[Unit] = {
    for {
      _ <- store.update(_ + (cbq.message.get.chat.id -> value))
    } yield ()
  }

  def setMessageState(value: S)(implicit msg: Message): F[Unit] = {
    for {
      _ <- store.update(_ + (msg.chat.id -> value))
    } yield ()
  }

  def withMessageState(f: F[S] => F[Unit])(implicit msg: Message): F[Unit] = f(getMessageState)

  def withCallbackState(f: F[S] => F[Unit])(implicit cbq: CallbackQuery): F[Unit] = f(getCallbackState)


  def getMessageState(implicit msg: Message): F[S] = {
    for {
      s <- store.get
      state = s.getOrElse(msg.chat.id, default)
    } yield state
  }

  def getCallbackState(implicit cbq: CallbackQuery): F[S] = {
    for {
      s <- store.get
      state = s.getOrElse(cbq.message.get.chat.id, default)
    } yield state
  }

}

object StateStore {
  def make[F[_] : Sync, S <: State](default: => S): F[StateStore[F, S]] = for {
    ref <- Ref.of[F, Map[Long, S]](Map.empty)
  } yield new StateStore[F, S](ref, default)
}