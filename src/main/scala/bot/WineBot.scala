package bot

import bot.domain.buttons._
import bot.domain.states._
import bot.domain.{Button, ButtonMarkup, Command}
import cats.effect.Async
import cats.syntax.flatMap._
import cats.syntax.functor._
import com.bot4s.telegram.Implicits._
import com.bot4s.telegram.api.declarative.{Callbacks, Commands, RegexCommands}
import com.bot4s.telegram.cats.{Polling, TelegramBot}
import com.bot4s.telegram.methods.{EditMessageReplyMarkup, EditMessageText, ParseMode, SendMessage}
import com.bot4s.telegram.models.{CallbackQuery, ChatId, InlineKeyboardMarkup, Message}
import database.DatabaseClient
import database.domain.Note
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

  onCommand(Command.Start.command) {
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


  onCommand(Command.Help.command) {
    implicit msg =>
      for {
        _ <- reply(
          text = messageFormatter.getHelpMessage,
          parseMode = ParseMode.Markdown,
        ).void
      } yield ()
  }

  onRegex(Command.Search.regularExpression) {
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

  onCommand(Command.Advise.command) {
    implicit msg =>
      perChatAdviseStateStore.withMessageState {
        state =>
          for {
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
      state =>
        for {
          _ <- handleAdviseSelectionCallback(
            CountryButton.Selection, ButtonMarkup.CountryMarkup,
            CountryButton.Clear, _ => state.copy(country = None),
            country => _ => state.copy(country = country),
          )
        } yield ()
    }
  }

  onCallbackWithTag(WineTypeButton.Selection.tag) { implicit cbq =>
    perChatAdviseStateStore.withCallbackState {
      state =>
        for {
          _ <- handleAdviseSelectionCallback(
            WineTypeButton.Selection, ButtonMarkup.WineTypeMarkup,
            WineTypeButton.Clear, _ => state.copy(wineType = None),
            wineType => _ => state.copy(wineType = wineType),
          )
        } yield ()
    }
  }


  onCallbackWithTag(PriceButtonType.Max.tag) { implicit cbq =>
    perChatAdviseStateStore.withCallbackState {
      state =>
        for {
          _ <- handleAdviseSelectionCallback(
            PriceButton.Selection(PriceButtonType.Max), ButtonMarkup.PriceMaxMarkup,
            PriceButton.Clear(PriceButtonType.Max), _ => state.copy(priceMax = None),
            priceMax => _ => state.copy(priceMax = priceMax.toIntOption),
          )
        } yield ()
    }
  }

  onCallbackWithTag(PriceButtonType.Min.tag) { implicit cbq =>
    perChatAdviseStateStore.withCallbackState {
      state =>
        for {
          _ <- handleAdviseSelectionCallback(
            PriceButton.Selection(PriceButtonType.Min), ButtonMarkup.PriceMinMarkup,
            PriceButton.Clear(PriceButtonType.Min), _ => state.copy(priceMin = None),
            priceMin => _ => state.copy(priceMin = priceMin.toIntOption),
          )
        } yield ()
    }
  }

  onCallbackWithTag(AdviseButton.Return.tag) { implicit cbq =>
    val editFuture = for {
      msg <- cbq.message
      _ <- editMarkup(msg, ButtonMarkup.AdviseMarkup)
    } yield ()
    editFuture.getOrElse(Async[F].pure()).void
  }

  onCallbackWithTag(AdviseButton.Clear.tag) { implicit cbq =>
    perChatAdviseStateStore.withCallbackState { _ =>
      for {
        newState <- perChatAdviseStateStore.setCallbackState(AdviseState.empty)
        _ <- editAdviseStateMessage(messageFormatter.getAdviseStateMessage(newState))
      } yield ()
    }
  }

  onCallbackWithTag(AdviseButton.Advise.tag) { implicit cbq =>
    perChatAdviseStateStore.withCallbackState { state =>
      val maybeEditFuture = for {
        _ <- for {
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
      } yield ()

      maybeEditFuture.getOrElse(Async[F].pure()).void
    }
  }

  onCommand(Command.Note.command) {
    implicit msg =>
      perChatNoteStateStore.withMessageState {
        state =>
          for {
            replyMessage <- reply(
              messageFormatter.getNoteStateMessage(state),
              replyMarkup = ButtonMarkup.NoteMarkup,
              parseMode = ParseMode.Markdown,
            )
            _ <- perChatNoteStateStore.setMessageState(
              state.updateMessageState(messageSource = replyMessage.source, messageId = replyMessage.messageId)
            )
          } yield ()
      }
  }


  onMessage { implicit message =>
    perChatNoteStateStore.withMessageState {
      state => {
        for {
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
      state =>
        for {
          getAndInsert <- for {
            userEitherOption <- databaseClient.getUser(cbq.from.id)
            userOption = userEitherOption.getOrElse(None)
            result = userOption
              .map(
                user => state.editMessageState match {
                  case Some(editMessageState) => editMessageState.noteDbId match {
                    case Some(noteId) => databaseClient.updateNote(noteId, state.wineName.getOrElse("-"), state.rating, state.price, state.review)
                    case None => databaseClient.insertNote(user.user_id, state.wineName.getOrElse("-"), state.rating, state.price, state.review)
                  }
                  case None => Async[F].pure()
                }
              )
              .getOrElse(Async[F].pure(Left()))
          } yield result
          dbResult <- getAndInsert
          _ <- dbResult match {
            case Left(_) => ackCallback(messageFormatter.getNoteSavedErrorMessage)
            case Right(_) => for {
              editMessageStateOption <- Async[F].pure(state.editMessageState)
              _ <- editMessageStateOption match {
                case Some(editMessageState) => request(
                  EditMessageReplyMarkup(
                    ChatId(editMessageState.messageSource.get),
                    editMessageState.messageId,
                    replyMarkup = None,
                  )
                ).void
                case None => Async[F].pure()
              }
              _ <- ackCallback(messageFormatter.getNoteSavedMessage(state.wineName.getOrElse("-")))
              _ <- perChatNoteStateStore.setCallbackState(NoteState.empty)
            } yield ()
          }
        } yield ()
    }
  }

  onCallbackWithTag(NoteButton.Clear.tag) { implicit cbq =>
    perChatNoteStateStore.withCallbackState { state =>
      for {
        newState <- perChatNoteStateStore.setCallbackState(
          NoteState.empty.copy(editMessageState = state.editMessageState)
        )
        _ <- editNoteStateMessage(newState).getOrElse(Async[F].pure())
      } yield ()
    }
  }

  private def getUserNotes(telegramId: Long): F[Either[String, List[Note]]] = for {
    userEitherOption <- databaseClient.getUser(telegram_id = telegramId)
    userOption = userEitherOption.getOrElse(None)
    result <- userOption match {
      case Some(user) => databaseClient.getNotes(user.user_id)
      case None => Async[F].pure(Left("Error"))
    }
  } yield result

  onCommand(Command.Notes.command) {
    implicit msg =>
      perChatNoteStateStore.withMessageState {
        state =>
          for {
            msgFrom <- Async[F].delay(msg.from)
            telegramUser = msgFrom.get
            notesEither <- getUserNotes(telegramUser.id)
            _ <- notesEither match {
              case Left(_) => reply(
                text = messageFormatter.getNotesErrorMessage,
                parseMode = ParseMode.Markdown,
              ).void
              case Right(notes) => for {
                _ <- perChatNoteStateStore.setMessageState(
                  state.updateNoteListState(notes, 0)
                )
                notesTrimmed = notes.take(Command.Notes.notesMaxCount)
                _ <- reply(
                  text = messageFormatter.getNotesList(notesTrimmed, 0),
                  parseMode = ParseMode.Markdown,
                  replyMarkup = ButtonMarkup.NotesMarkup(notesTrimmed, 0, Command.Notes.notesMaxCount),
                ).void
              } yield ()
            }
          } yield ()
      }
  }

  onCallbackWithTag(NotesButtons.ControlButtonsTag) { implicit cbq =>
    perChatNoteStateStore.withCallbackState {
      state =>
        val editFuture = for {
          data <- cbq.data
          nextPage = data match {
            case NotesButtons.Next.name => (x: Int) => x + 1
            case NotesButtons.Previous.name => (x: Int) => x - 1
          }
          noteListState <- state.notesListState
          page <- nextPage(noteListState.noteIdx)
          startFrom = page * 10
          notes <- Async[F].pure(noteListState.notes.slice(startFrom, startFrom + 10))
          _ <- notes match {
            case _ :: _ => for {
              _ <- request(
                EditMessageText(
                  ChatId(cbq.message.get.source),
                  cbq.message.get.messageId,
                  text = messageFormatter.getNotesList(notes, page),
                  parseMode = ParseMode.Markdown,
                  replyMarkup = ButtonMarkup.NotesMarkup(notes, page, Command.Notes.notesMaxCount),
                )
              ).void
              _ <- perChatNoteStateStore.setCallbackState(state.updateNoteListState(noteIdx = page))
            } yield ()
            case Nil => for {
              _ <- ackCallback(messageFormatter.getNotesOutOfBoundaryErrorMessage)
            } yield ()
          }
        } yield ()
        editFuture.getOrElse(Async[F].pure()).void
    }
  }

  onCallbackWithTag(NotesButtons.SelectNotesTag) { implicit cbq =>
    perChatNoteStateStore.withCallbackState {
      state =>
        val editFuture = for {
          source <- cbq.message.get.source
          messageId <- cbq.message.get.messageId
          noteId <- cbq.data.map(_.toInt)
          noteListState <- state.notesListState
          note <- noteListState.notes(noteId)
          state <- perChatNoteStateStore.setCallbackState(state.fromNote(note))
          _ <- request(
            EditMessageText(
              chatId = ChatId(source),
              messageId = messageId,
              text = messageFormatter.getNoteStateMessage(state),
              replyMarkup = ButtonMarkup.NoteMarkup,
              parseMode = ParseMode.Markdown,
            )
          )
          _ <- perChatNoteStateStore.setCallbackState(
            state.updateMessageState(source, messageId, note.id)
          )
        } yield ()
        editFuture.getOrElse(Async[F].pure()).void
    }
  }

  onCallbackWithTag(NoteButton.Return.tag) { implicit cbq =>
    perChatNoteStateStore.withCallbackState {
      state =>
        val editFuture = for {
          telegramId <- cbq.from.id
          noteListStateOption <- state.notesListState match {
            case state@Some(_) => Async[F].pure(state)
            case none@None => for {
              notesEither <- getUserNotes(telegramId)
              res <- notesEither match {
                case Left(_) => Async[F].pure(none)
                case Right(notes) => for {
                  newState <- perChatNoteStateStore.setCallbackState(
                    state.updateNoteListState(notes, 0)
                  )
                } yield newState.notesListState
              }
            } yield res
          }
          _ <- noteListStateOption match {
            case Some(noteListState) => for {
              page <- Async[F].pure(noteListState.noteIdx)
              startFrom = page * 10
              notes = noteListState.notes.slice(startFrom, startFrom + 10)
              _ <- request(
                EditMessageText(
                  ChatId(cbq.message.get.source),
                  cbq.message.get.messageId,
                  text = messageFormatter.getNotesList(notes, page),
                  parseMode = ParseMode.Markdown,
                  replyMarkup = ButtonMarkup.NotesMarkup(notes, page, Command.Notes.notesMaxCount),
                )
              ).void
            } yield ()
            case None => Async[F].delay(ackCallback(messageFormatter.getNotesErrorMessage))
          }
        } yield ()

        editFuture.getOrElse(Async[F].pure())
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
      _ <- editNoteStateMessage(state).getOrElse(Async[F].pure())
      _ <- perChatNoteStateStore.setMessageState(state.copy(status = Empty))
    } yield ()

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

  private def handleAdviseSelectionCallback(
    selectionButton: Button, selectionMarkup: InlineKeyboardMarkup,
    clearButton: Button, clearStateFunc: AdviseState => AdviseState,
    updateStateFunc: String => AdviseState => AdviseState,
  )(implicit cbq: CallbackQuery) = {
    val maybeEditFuture = for {
      data <- cbq.data
      msg <- cbq.message
      _ <- data match {
        case button if button == selectionButton.name => editMarkup(msg, selectionMarkup)
        case button => for {
          state <- perChatAdviseStateStore.getCallbackState
          newState = if (button == clearButton.name) {
            clearStateFunc(state)
          } else {
            updateStateFunc(data)(state)
          }
          _ <- perChatAdviseStateStore.setCallbackState(newState)
          _ <- editAdviseStateMessage(messageFormatter.getAdviseStateMessage(newState))
        } yield ()
      }
    } yield ()

    maybeEditFuture.getOrElse(Async[F].pure()).void
  }

  private def editMarkup(msg: Message, keyboard: InlineKeyboardMarkup) = {
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

  private def editNoteStateMessage(state: NoteState) = {
    for {
      editMessageState <- state.editMessageState
      _ <- request(
        EditMessageText(
          ChatId(editMessageState.messageSource.get),
          editMessageState.messageId,
          text = messageFormatter.getNoteStateMessage(state),
          replyMarkup = ButtonMarkup.NoteMarkup,
          parseMode = ParseMode.Markdown,
        )
      ).void
    } yield ()
  }
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