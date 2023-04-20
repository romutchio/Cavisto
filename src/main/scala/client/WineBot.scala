package client

import bot.domain.AdviseState
import bot.domain.buttons._
import bot.{MessageFormatter, PerChatState}
import cats.effect.Async
import cats.syntax.flatMap._
import cats.syntax.functor._
import com.bot4s.telegram.Implicits._
import com.bot4s.telegram.api.declarative.{Callbacks, Commands, RegexCommands}
import com.bot4s.telegram.cats.{Polling, TelegramBot}
import com.bot4s.telegram.methods.{EditMessageReplyMarkup, EditMessageText, ParseMode}
import com.bot4s.telegram.models.{CallbackQuery, ChatId, InlineKeyboardMarkup, Message}
import org.asynchttpclient.Dsl.asyncHttpClient
import sttp.client3.asynchttpclient.cats.AsyncHttpClientCatsBackend
import vivino.WineClient
import vivino.domain.CurrencyCode.Euro

class WineBot[F[_] : Async](token: String)(implicit val wineClient: WineClient[F], messageFormatter: MessageFormatter)
  extends TelegramBot[F](token, AsyncHttpClientCatsBackend.usingClient[F](asyncHttpClient()))
    with Polling[F]
    with Commands[F]
    with RegexCommands[F]
    with PerChatState[F]
    with Callbacks[F] {


  onRegex("""/search+\s(.+)""".r) {
    implicit msg => {
      case Seq(input: String) =>
        for {
          m <- replyMd(messageFormatter.winesSearch(input), replyToMessageId = msg.messageId)
          wines <- wineClient.getWinesByName(input)
          _ <- wines match {
            case _ :: _ => request(
              EditMessageText(
                ChatId(m.source),
                m.messageId,
                text = messageFormatter.winesToMessage(wines),
                parseMode = ParseMode.Markdown,
              )
            )
            case Nil => request(
              EditMessageText(
                ChatId(m.source),
                m.messageId,
                text = messageFormatter.winesToMessageNotFound(input),
                parseMode = ParseMode.Markdown,
              )
            )
          }
        } yield ()
    }
  }

  onCommand("/advise") {
    implicit msg =>
      withMessageState {
        s => {
          reply(
            messageFormatter.adviseStateToMessage(s),
            replyMarkup = ButtonMarkup.AdviseMarkup,
          ).void
        }
      }
  }

  onCallbackWithTag(CountryButton.Selection.tag) { implicit cbq =>
    withCallbackState {
      s =>
        createCallback(
          cbq,
          CountryButton.Selection, ButtonMarkup.CountryMarkup,
          CountryButton.Clear, s.copy(country = None),
          x => s.copy(country = x),
        )
    }
  }

  onCallbackWithTag(WineTypeButton.Selection.tag) { implicit cbq =>
    withCallbackState {
      s =>
        createCallback(
          cbq,
          WineTypeButton.Selection, ButtonMarkup.WineTypeMarkup,
          WineTypeButton.Clear, s.copy(wineType = None),
          x => s.copy(wineType = x),
        )
    }
  }


  onCallbackWithTag(PriceButtonType.Max.tag) { implicit cbq =>
    withCallbackState { s =>
      createCallback(cbq,
        PriceButton.Selection(PriceButtonType.Max), ButtonMarkup.PriceMaxMarkup,
        PriceButton.Clear(PriceButtonType.Max), s.copy(priceMax = None),
        x => s.copy(priceMax = x.toIntOption),
      )
    }
  }

  onCallbackWithTag(PriceButtonType.Min.tag) { implicit cbq =>
    withCallbackState { s =>
      createCallback(cbq,
        PriceButton.Selection(PriceButtonType.Min), ButtonMarkup.PriceMinMarkup,
        PriceButton.Clear(PriceButtonType.Min), s.copy(priceMin = None),
        x => s.copy(priceMin = x.toIntOption),
      )
    }
  }

  onCallbackWithTag(AdviseButton.Advise.tag) { implicit cbq =>
    withCallbackState { s =>
      val maybeEditFuture = for {
        data <- cbq.data
        msg <- cbq.message
        _ <- data match {
          case x if x == AdviseButton.Return.name =>
            createSelectionMarkupRequest(msg, ButtonMarkup.AdviseMarkup)

          case x if x == AdviseButton.Advise.name =>
            for {
              wines <- wineClient.adviseWine(
                s.getCountryCode,
                Euro,
                s.getWineType,
                3,
                s.priceMin,
                s.priceMax
              )
              _ <- wines match {
                case _ :: _ =>
                  createAdviseStateMessageRequest(
                    msg,
                    messageFormatter.winesToMessage(wines),
                  )
                case Nil => createAdviseStateMessageRequest(
                  msg,
                  messageFormatter.adviseStateToMessageNotFound(s),
                )
              }
            } yield ()
        }
      } yield ()

      maybeEditFuture.getOrElse(Async[F].pure()).void
    }
  }

  private def createCallback(
    implicit cbq: CallbackQuery,
    selectionButton: Button,
    selectionMarkup: InlineKeyboardMarkup,
    clearButton: Button,
    clearStateFunc: => AdviseState,
    updateStateFunc: String => AdviseState,
  ) = {
    val maybeEditFuture = for {
      data <- cbq.data
      msg <- cbq.message
      _ <- data match {
        case x if x == selectionButton.name =>
          createSelectionMarkupRequest(msg, selectionMarkup)
        case x if x == clearButton.name =>
          updateStateAndCreateRequest(cbq, msg, clearStateFunc)
        case _ =>
          updateStateAndCreateRequest(cbq, msg, updateStateFunc(data))
      }
    } yield ()

    maybeEditFuture.getOrElse(Async[F].pure()).void
  }

  private def updateStateAndCreateRequest(implicit cbq: CallbackQuery, msg: Message, updateStateFunc: => AdviseState) = {
    val newState = updateStateFunc
    setCallbackState(newState)
    createAdviseStateMessageRequest(msg, messageFormatter.adviseStateToMessage(newState))
  }


  private def createSelectionMarkupRequest(msg: Message, keyboard: InlineKeyboardMarkup) = {
    request(
      EditMessageReplyMarkup(
        ChatId(msg.source),
        msg.messageId,
        replyMarkup = keyboard,
      )
    )
  }

  private def createAdviseStateMessageRequest(msg: Message, text: String) = {
    request(
      EditMessageText(
        ChatId(msg.source),
        msg.messageId,
        text = text,
        replyMarkup = ButtonMarkup.AdviseMarkup,
        parseMode = ParseMode.Markdown,
      )
    )
  }
}
