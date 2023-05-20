import bot.domain.states.{AdviseState, NoteState, WineListState}
import bot.{MessageFormatter, StateStore, WineBot}
import cats.effect.{ExitCode, IO, IOApp}
import client.Http4sHttpClient
import database.DoobieDatabaseClient
import vivino.VivinoWineClient
import vivino.parser.JsoupVivinoHtmlParser


object Main extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
    for {
      jsoupVivinoHTMLParser <- JsoupVivinoHtmlParser.make[IO]
      httpClient <- Http4sHttpClient.make[IO]
      vivinoClient <- VivinoWineClient.make[IO](jsoupVivinoHTMLParser, httpClient)
      messageFormatter = MessageFormatter.make
      adviseStateStore <- StateStore.make[IO, AdviseState](AdviseState.empty)
      noteStateStore <- StateStore.make[IO, NoteState](NoteState.empty)
      searchStateStore <- StateStore.make[IO, WineListState](WineListState.empty)
      token <- IO.fromOption(sys.env.get("TELEGRAM_TOKEN"))(
        new Exception("TELEGRAM_TOKEN environment variable not set")
      )
      databaseClient <- DoobieDatabaseClient.make[IO]
      wineBot <- WineBot.make[IO](token, vivinoClient, messageFormatter, adviseStateStore, noteStateStore, searchStateStore, databaseClient)
      _ <- wineBot.startPolling().map(_ => ExitCode.Success)
    } yield ExitCode.Success
  }
}
