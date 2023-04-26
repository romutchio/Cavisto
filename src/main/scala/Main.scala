import bot.{AdviseStateStore, MessageFormatter}
import cats.effect.{ExitCode, IO, IOApp}
import client.{Http4sHttpClient, WineBot}
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
      wineBot <- WineBot.make[IO](token, vivinoClient, messageFormatter, store)
      _ <- wineBot.startPolling().map(_ => ExitCode.Success)
    } yield ExitCode.Success
  }
}
