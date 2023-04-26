package bot

import bot.domain.AdviseState
import com.bot4s.telegram.models.{CallbackQuery, Message}

trait PerChatState[F[_]] {
  def withMessageState(f: F[AdviseState] => F[Unit])(implicit msg: Message, store: AdviseStateStore[F]): F[Unit] =
    f(store.getMessageState)

  def withCallbackState(f: F[AdviseState] => F[Unit])(implicit cbq: CallbackQuery, store: AdviseStateStore[F]): F[Unit] =
    f(store.getCallbackState)

}
