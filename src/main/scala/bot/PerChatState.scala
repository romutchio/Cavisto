package bot

import bot.domain.AdviseState
import com.bot4s.telegram.models.{CallbackQuery, Message}


trait PerChatState[F[_]] {
  private val chatState = collection.mutable.Map[Long, AdviseState]()

  def setChatState(value: AdviseState)(implicit msg: Message): Unit = atomic {
    chatState.update(msg.chat.id, value)
  }

  def setCallbackState(value: AdviseState)(implicit cbq: CallbackQuery): Unit = atomic {
    chatState.update(cbq.message.get.chat.id, value)
  }

  private def atomic[T](f: => T): T = chatState.synchronized {
    f
  }

  def withMessageState(f: AdviseState => F[Unit])(implicit msg: Message): F[Unit] = f(getMessageState)

  def withCallbackState(f: AdviseState => F[Unit])(implicit cbq: CallbackQuery): F[Unit] = f(getCallbackState)

  private def getMessageState(implicit msg: Message): AdviseState = atomic {
    chatState.getOrElseUpdate(msg.chat.id, AdviseState(None, None, None, None))
  }

  private def getCallbackState(implicit cbq: CallbackQuery): AdviseState = atomic {
    chatState.getOrElseUpdate(cbq.message.get.chat.id, AdviseState(None, None, None, None))
  }
}