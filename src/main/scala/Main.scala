import bot.{AdviseStateStore, MessageFormatter}
import cats.effect.{ExitCode, IO, IOApp}
import client.{Http4sHttpClient, WineBot}
import database.DoobieDatabaseClient
import parser.JsoupVivinoHtmlParser
import vivino.VivinoWineClient


object Main extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
    for {
      jsoupVivinoHTMLParser <- JsoupVivinoHtmlParser.make[IO]
      httpClient <- Http4sHttpClient.make[IO]
      vivinoClient <- VivinoWineClient.make[IO](jsoupVivinoHTMLParser, httpClient)
      messageFormatter = MessageFormatter.make
      store <- AdviseStateStore.make[IO]
      token <- IO.fromOption(sys.env.get("TELEGRAM_TOKEN"))(
        new Exception("TELEGRAM_TOKEN environment variable not set")
      )
      databaseClient <- DoobieDatabaseClient.make[IO]
      wineBot <- WineBot.make[IO](token, vivinoClient, messageFormatter, store, databaseClient)
      _ <- wineBot.startPolling().map(_ => ExitCode.Success)
    } yield ExitCode.Success
  }
}
