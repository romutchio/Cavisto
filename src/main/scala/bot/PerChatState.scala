package bot

import bot.domain.states.State
import com.bot4s.telegram.models.{CallbackQuery, Message}

trait PerChatState[F[_], S <: State] {
  def withMessageState(f: F[S] => F[Unit])(implicit msg: Message, store: StateStore[F, S]): F[Unit] =
    f(store.getMessageState)

  def withCallbackState(f: F[S] => F[Unit])(implicit cbq: CallbackQuery, store: StateStore[F, S]): F[Unit] =
    f(store.getCallbackState)

}
