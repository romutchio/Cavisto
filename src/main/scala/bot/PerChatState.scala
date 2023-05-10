package bot

import bot.domain.states.State
import com.bot4s.telegram.models.{CallbackQuery, Message}

trait PerChatState[F[_], S <: State] {
  def withMessageState(f: S => F[Unit])(implicit msg: Message, store: StateStore[F, S]): F[Unit] =
    store.withMessageState(f)

  def withCallbackState(f: S => F[Unit])(implicit cbq: CallbackQuery, store: StateStore[F, S]): F[Unit] =
    store.withCallbackState(f)

}
