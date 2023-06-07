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
      dbHost <- IO.fromOption(sys.env.get("DB_HOST"))(new Exception("DB_HOST environment variable not set"))
      dbPort <- IO.fromOption(sys.env.get("DB_PORT"))(new Exception("DB_PORT environment variable not set"))
      dbUser <- IO.fromOption(sys.env.get("DB_USER"))(new Exception("DB_USER environment variable not set"))
      dbPass <- IO.fromOption(sys.env.get("DB_PASS"))(new Exception("DB_PASS environment variable not set"))
      dbName <- IO.fromOption(sys.env.get("DB_NAME"))(new Exception("DB_NAME environment variable not set"))
      databaseClient <- DoobieDatabaseClient.make[IO](dbHost, dbPort, dbUser, dbPass, dbName)
      wineBot <- WineBot.make[IO](token, vivinoClient, messageFormatter, adviseStateStore, noteStateStore, searchStateStore, databaseClient)
      _ <- wineBot.startPolling().map(_ => ExitCode.Success)
    } yield ExitCode.Success
  }
}
