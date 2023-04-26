package client

import bot.domain.AdviseState
import bot.domain.buttons._
import bot.{AdviseStateStore, MessageFormatter, PerChatState}
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

class WineBot[F[_] : Async](token: String)(
  wineClient: WineClient[F],
  messageFormatter: MessageFormatter,
  store: AdviseStateStore[F],
)
  extends TelegramBot[F](token, AsyncHttpClientCatsBackend.usingClient[F](asyncHttpClient()))
    with Polling[F]
    with Commands[F]
    with RegexCommands[F]
    with PerChatState[F]
    with Callbacks[F] {

  implicit val perChatStateStore: AdviseStateStore[F] = store


  onRegex("""/search+\s(.+)""".r) {
    implicit msg => {
      case Seq(input: String) =>
        for {
          m <- replyMd(messageFormatter.getSearchWinesMessage(input), replyToMessageId = msg.messageId)
          wines <- wineClient.getWinesByName(input)
          _ <- wines match {
            case _ :: _ => request(
              EditMessageText(
                ChatId(m.source),
                m.messageId,
                text = messageFormatter.getWinesFoundMessage(wines),
                parseMode = ParseMode.Markdown,
              )
            )
            case Nil => request(
              EditMessageText(
                ChatId(m.source),
                m.messageId,
                text = messageFormatter.getWinesNotFoundMessage(input),
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
        stateF =>
          for {
            state <- stateF
            _ <- reply(
              messageFormatter.getAdviseStateMessage(state),
              replyMarkup = ButtonMarkup.AdviseMarkup,
            ).void
          } yield ()
      }
  }

  onCallbackWithTag(CountryButton.Selection.tag) { implicit cbq =>
    withCallbackState {
      stateF =>
        for {
          state <- stateF
          _ <- createCallback(
            CountryButton.Selection, ButtonMarkup.CountryMarkup,
            CountryButton.Clear, _ => state.copy(country = None),
            x => _ => state.copy(country = x),
          )
        } yield ()
    }
  }

  onCallbackWithTag(WineTypeButton.Selection.tag) { implicit cbq =>
    withCallbackState {
      stateF =>
        for {
          state <- stateF
          _ <- createCallback(
            WineTypeButton.Selection, ButtonMarkup.WineTypeMarkup,
            WineTypeButton.Clear, _ => state.copy(wineType = None),
            wineType => _ => state.copy(wineType = wineType),
          )
        } yield ()
    }
  }


  onCallbackWithTag(PriceButtonType.Max.tag) { implicit cbq =>
    withCallbackState {
      stateF =>
        for {
          state <- stateF
          _ <- createCallback(
            PriceButton.Selection(PriceButtonType.Max), ButtonMarkup.PriceMaxMarkup,
            PriceButton.Clear(PriceButtonType.Max), _ => state.copy(priceMax = None),
            priceMax => _ => state.copy(priceMax = priceMax.toIntOption),
          )
        } yield ()
    }
  }

  onCallbackWithTag(PriceButtonType.Min.tag) { implicit cbq =>
    withCallbackState {
      stateF =>
        for {
          state <- stateF
          _ <- createCallback(
            PriceButton.Selection(PriceButtonType.Min), ButtonMarkup.PriceMinMarkup,
            PriceButton.Clear(PriceButtonType.Min), _ => state.copy(priceMin = None),
            priceMin => _ => state.copy(priceMin = priceMin.toIntOption),
          )
        } yield ()
    }
  }

  onCallbackWithTag(AdviseButton.Advise.tag) { implicit cbq =>
    withCallbackState { stateF =>
      val maybeEditFuture = for {
        data <- cbq.data
        msg <- cbq.message
        _ <- data match {
          case x if x == AdviseButton.Return.name =>
            createSelectionMarkupRequest(msg, ButtonMarkup.AdviseMarkup)

          case x if x == AdviseButton.Advise.name =>
            for {
              state <- stateF
              wines <- wineClient.adviseWine(
                state.getCountryCode,
                Euro,
                state.getWineType,
                3,
                state.priceMin,
                state.priceMax
              )
              _ <- wines match {
                case _ :: _ =>
                  createAdviseStateMessageRequest(
                    msg,
                    messageFormatter.getWinesFoundMessage(wines),
                  )
                case Nil => createAdviseStateMessageRequest(
                  msg,
                  messageFormatter.getAdviseStateMessageWinesNotFound(state),
                )
              }
            } yield ()
        }
      } yield ()

      maybeEditFuture.getOrElse(Async[F].pure()).void
    }
  }

  private def createCallback(
    selectionButton: Button,
    selectionMarkup: InlineKeyboardMarkup,
    clearButton: Button,
    clearStateFunc: AdviseState => AdviseState,
    updateStateFunc: String => AdviseState => AdviseState,
  )(implicit cbq: CallbackQuery) = {
    val maybeEditFuture = for {
      data <- cbq.data
      msg <- cbq.message
      _ <- data match {
        case x if x == selectionButton.name =>
          createSelectionMarkupRequest(msg, selectionMarkup)
        case x if x == clearButton.name => for {
          state <- perChatStateStore.getCallbackState
          newState = clearStateFunc(state)
          _ <- perChatStateStore.setCallbackState(newState)
          _ <- createAdviseStateMessageRequest(msg, messageFormatter.getAdviseStateMessage(newState))
        } yield ()
        case _ => for {
          state <- perChatStateStore.getCallbackState
          newState = updateStateFunc(data)(state)
          _ <- perChatStateStore.setCallbackState(newState)
          _ <- createAdviseStateMessageRequest(msg, messageFormatter.getAdviseStateMessage(newState))
        } yield ()
      }
    } yield ()

    maybeEditFuture.getOrElse(Async[F].pure()).void
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


object WineBot {
  def make[F[_] : Async](token: String, wineClient: WineClient[F], messageFormatter: MessageFormatter, store: AdviseStateStore[F]): F[WineBot[F]] =
    Async[F].delay(new WineBot[F](token)(wineClient, messageFormatter, store))
}