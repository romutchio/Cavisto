package bot.domain.buttons

import com.bot4s.telegram.models.InlineKeyboardButton

trait Button {
  val emoji: Option[String]
  val name: String
  val tag: String

  private def button: String = (emoji, name) match {
    case (Some(e), n) => s"$e $n"
    case (None, n) => n
  }

  private def prefixTag(tag: String)(s: String): String = tag + s

  def toKeyboardButton: InlineKeyboardButton =
    InlineKeyboardButton.callbackData(this.button, prefixTag(tag)(this.name))
}
