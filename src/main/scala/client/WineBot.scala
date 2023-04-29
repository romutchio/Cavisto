package client

import bot.domain.buttons._
import bot.domain.states._
import bot.{MessageFormatter, StateStore}
import cats.effect.Async
import cats.syntax.flatMap._
import cats.syntax.functor._
import com.bot4s.telegram.Implicits._
import com.bot4s.telegram.api.declarative.{Callbacks, Commands, RegexCommands}
import com.bot4s.telegram.cats.{Polling, TelegramBot}
import com.bot4s.telegram.methods.{EditMessageReplyMarkup, EditMessageText, ParseMode, SendMessage}
import com.bot4s.telegram.models.{CallbackQuery, ChatId, InlineKeyboardMarkup, Message}
import database.DatabaseClient
import org.asynchttpclient.Dsl.asyncHttpClient
import sttp.client3.asynchttpclient.cats.AsyncHttpClientCatsBackend
import vivino.WineClient
import vivino.domain.CurrencyCode.Euro

class WineBot[F[_] : Async](token: String)(
  wineClient: WineClient[F],
  messageFormatter: MessageFormatter,
  adviseStateStore: StateStore[F, AdviseState],
  noteStateStore: StateStore[F, NoteState],
  databaseClient: DatabaseClient[F],
)
  extends TelegramBot[F](token, AsyncHttpClientCatsBackend.usingClient[F](asyncHttpClient()))
    with Polling[F]
    with Commands[F]
    with RegexCommands[F]
    with Callbacks[F] {

  implicit val perChatAdviseStateStore: StateStore[F, AdviseState] = adviseStateStore
  implicit val perChatNoteStateStore: StateStore[F, NoteState] = noteStateStore

  onCommand("/start") {
    implicit msg =>
      for {
        msgFrom <- Async[F].delay(msg.from)
        telegramUser = msgFrom.get
        userInsert <- databaseClient.insertUser(
          telegram_id = telegramUser.id,
          username = telegramUser.username,
          firstName = telegramUser.firstName,
          lastName = telegramUser.lastName,
        )
        _ <- userInsert match {
          case Left(_) => reply(
            text = messageFormatter.getUserSavedErrorMessage,
            parseMode = ParseMode.Markdown,
          ).void
          case Right(_) => reply(
            text = messageFormatter.getWelcomeMessage(
              telegramUser.username,
              telegramUser.firstName,
              telegramUser.lastName,
            ),
            parseMode = ParseMode.Markdown,
          ).void
        }
      } yield ()
  }

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
      perChatAdviseStateStore.withMessageState {
        stateF =>
          for {
            state <- stateF
            _ <- reply(
              messageFormatter.getAdviseStateMessage(state),
              replyMarkup = ButtonMarkup.AdviseMarkup,
              parseMode = ParseMode.Markdown,
            ).void
          } yield ()
      }
  }

  onCallbackWithTag(CountryButton.Selection.tag) { implicit cbq =>
    perChatAdviseStateStore.withCallbackState {
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
    perChatAdviseStateStore.withCallbackState {
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
    perChatAdviseStateStore.withCallbackState {
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
    perChatAdviseStateStore.withCallbackState {
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

  onCallbackWithTag(AdviseButton.Clear.tag) { implicit cbq =>
    perChatAdviseStateStore.withCallbackState { stateF =>
      for {
        state <- perChatAdviseStateStore.setCallbackState(AdviseState.empty)
        _ <- editAdviseStateMessage(messageFormatter.getAdviseStateMessage(state))
      } yield ()
    }
  }

  onCallbackWithTag(NoteButton.Clear.tag) { implicit cbq =>
    perChatNoteStateStore.withCallbackState { stateF =>
      for {
        state <- stateF
        state <- perChatNoteStateStore.setCallbackState(
          NoteState.empty.copy(messageId = state.messageId, messageSource = state.messageSource)
        )
        _ <- editNoteStateMessage(state)
      } yield ()
    }
  }


  onCallbackWithTag(AdviseButton.Advise.tag) { implicit cbq =>
    perChatAdviseStateStore.withCallbackState { stateF =>
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
                case _ :: _ => editAdviseStateMessage(messageFormatter.getWinesFoundMessage(wines))
                case Nil => editAdviseStateMessage(messageFormatter.getAdviseStateMessageWinesNotFound(state))
              }
              getAndInsert <- for {
                userEitherOption <- databaseClient.getUser(cbq.from.id)
                userOption = userEitherOption.getOrElse(None)
                result = userOption
                  .map(user => databaseClient.insertAdviseHistory(user.user_id, state))
                  .getOrElse(Async[F].pure(Left()))
              } yield result
              dbResult <- getAndInsert
              _ <- dbResult match {
                case Left(_) => ackCallback(messageFormatter.getAdviseHistorySavedErrorMessage)
                case Right(_) => Async[F].pure()
              }
            } yield ()
        }
      } yield ()

      maybeEditFuture.getOrElse(Async[F].pure()).void
    }
  }

  onCommand("/note") {
    implicit msg =>
      perChatNoteStateStore.withMessageState {
        stateF =>
          for {
            state <- stateF
            replyMessage <- reply(
              messageFormatter.getNoteStateMessage(state),
              replyMarkup = ButtonMarkup.NoteMarkup,
              parseMode = ParseMode.Markdown,
            )
            _ <- perChatNoteStateStore.setMessageState(
              state.copy(messageId = replyMessage.messageId, messageSource = replyMessage.source)
            )
          } yield ()
      }
  }

  private def handleNoteStateEditMessage(updateField: NoteState => NoteState, editInfoMessage: String)(implicit message: Message) =
    for {
      state <- perChatNoteStateStore.getMessageState
      _ <- perChatNoteStateStore.setMessageState(updateField(state))
      _ <- request(
        SendMessage(
          message.get.chat.chatId,
          editInfoMessage,
        )
      ).void
      state <- perChatNoteStateStore.getMessageState
      _ <- editNoteStateMessage(state)
      _ <- perChatNoteStateStore.setMessageState(state.copy(status = Empty))
    } yield ()

  onMessage { implicit message =>
    perChatNoteStateStore.withMessageState {
      stateF => {
        for {
          state <- stateF
          _ <- state.status match {
            case AwaitingWineNameEdit => for {
              _ <- handleNoteStateEditMessage(
                s => s.copy(wineName = message.text),
                messageFormatter.getEditAppliedMessage("Wine name", message.text.get),
              )
            } yield ()

            case AwaitingReviewEdit => for {
              _ <- handleNoteStateEditMessage(
                s => s.copy(review = message.text),
                messageFormatter.getEditAppliedMessage("Review", message.text.get),
              )
            } yield ()

            case AwaitingPriceEdit =>
              for {
                _ <- handleNoteStateEditMessage(
                  s => s.copy(price = message.text.flatMap(_.toDoubleOption)),
                  messageFormatter.getEditAppliedMessage("Price", message.text.get),
                )
              } yield ()

            case AwaitingRatingEdit =>
              for {
                _ <- handleNoteStateEditMessage(
                  s => s.copy(rating = message.text.flatMap(_.toDoubleOption)),
                  messageFormatter.getEditAppliedMessage("Rating", message.text.get),
                )
              } yield ()

            case Empty => Async[F].pure()
          }
        } yield ()
      }
    }
  }

  onCallbackWithTag(EditNoteButton.Name.tag) { implicit cbq =>
    for {
      _ <- handleEditButtonCallback(
        state => state.copy(status = AwaitingWineNameEdit),
        messageFormatter.getEditWineNameMessage,
      )
    } yield ()
  }

  onCallbackWithTag(EditNoteButton.Review.tag) { implicit cbq =>
    for {
      _ <- handleEditButtonCallback(
        state => state.copy(status = AwaitingReviewEdit),
        messageFormatter.getEditReviewMessage,
      )
    } yield ()
  }

  onCallbackWithTag(EditNoteButton.Rating.tag) { implicit cbq =>
    for {
      _ <- handleEditButtonCallback(
        state => state.copy(status = AwaitingRatingEdit),
        messageFormatter.getEditRatingMessage,
      )
    } yield ()
  }

  onCallbackWithTag(EditNoteButton.Price.tag) { implicit cbq =>
    for {
      _ <- handleEditButtonCallback(
        state => state.copy(status = AwaitingPriceEdit),
        messageFormatter.getEditPriceMessage,
      )
    } yield ()
  }

  onCallbackWithTag(NoteButton.Save.tag) { implicit cbq =>
    perChatNoteStateStore.withCallbackState {
      stateF =>
        for {
          state <- stateF
          getAndInsert <- for {
            userEitherOption <- databaseClient.getUser(cbq.from.id)
            userOption = userEitherOption.getOrElse(None)
            result = userOption
              .map(
                user =>
                  databaseClient.insertNote(user.user_id, state.wineName.getOrElse("-"), state.rating, state.price, state.review)
              )
              .getOrElse(Async[F].pure(Left()))
          } yield result
          dbResult <- getAndInsert
          _ <- dbResult match {
            case Left(_) => ackCallback(messageFormatter.getNoteSavedErrorMessage)
            case Right(_) => for {
              _ <- request(
                EditMessageReplyMarkup(
                  ChatId(state.messageSource),
                  state.messageId,
                  replyMarkup = None,
                )
              ).void
              _ <- ackCallback(messageFormatter.getNoteSavedMessage(state.wineName.getOrElse("-")))
              _ <- perChatNoteStateStore.setCallbackState(NoteState.empty)
            } yield ()
          }
        } yield ()
    }
  }

  private def handleEditButtonCallback(changeStatus: NoteState => NoteState, changeStatusMessage: String)(implicit cbq: CallbackQuery) =
    for {
      state <- perChatNoteStateStore.getCallbackState
      _ <- perChatNoteStateStore.setCallbackState(changeStatus(state))
      _ <- request(
        SendMessage(
          cbq.message.get.chat.chatId,
          changeStatusMessage,
        )
      ).void
    } yield ()

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
          state <- perChatAdviseStateStore.getCallbackState
          newState = clearStateFunc(state)
          _ <- perChatAdviseStateStore.setCallbackState(newState)
          _ <- editAdviseStateMessage(messageFormatter.getAdviseStateMessage(newState))
        } yield ()
        case _ => for {
          state <- perChatAdviseStateStore.getCallbackState
          newState = updateStateFunc(data)(state)
          _ <- perChatAdviseStateStore.setCallbackState(newState)
          _ <- editAdviseStateMessage(messageFormatter.getAdviseStateMessage(newState))
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

  private def editAdviseStateMessage(text: String)(implicit cbq: CallbackQuery) = {
    request(
      EditMessageText(
        ChatId(cbq.message.get.source),
        cbq.message.get.messageId,
        text = text,
        replyMarkup = ButtonMarkup.AdviseMarkup,
        parseMode = ParseMode.Markdown,
      )
    )
  }

  private def editNoteStateMessage(state: NoteState) =
    request(
      EditMessageText(
        ChatId(state.messageSource),
        state.messageId,
        text = messageFormatter.getNoteStateMessage(state),
        replyMarkup = ButtonMarkup.NoteMarkup,
        parseMode = ParseMode.Markdown,
      )
    ).void
}


object WineBot {
  def make[F[_] : Async](
    token: String,
    wineClient: WineClient[F],
    messageFormatter: MessageFormatter,
    adviseStateStore: StateStore[F, AdviseState],
    noteStateStore: StateStore[F, NoteState],
    databaseClient: DatabaseClient[F],
  ): F[WineBot[F]] =
    Async[F].delay(new WineBot[F](token)(wineClient, messageFormatter, adviseStateStore, noteStateStore, databaseClient))
}