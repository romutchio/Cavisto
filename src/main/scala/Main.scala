import bot.MessageFormatter
import cats.effect.{ExitCode, IO, IOApp}
import client.{Http4sHttpClient, WineBot}
import parser.JsoupVivinoHtmlParser
import vivino.VivinoWineClient


object Main extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
    implicit val jsoupVivinoHTMLParser: JsoupVivinoHtmlParser[IO] = new JsoupVivinoHtmlParser[IO]()
    implicit val httpClient: Http4sHttpClient[IO] = new Http4sHttpClient[IO]()
    implicit val vivinoClient: VivinoWineClient[IO] = new VivinoWineClient[IO]()
    implicit val messageFormatter: MessageFormatter = new MessageFormatter()

    for {
      token <- IO.fromOption(sys.env.get("TELEGRAM_TOKEN"))(new Exception("TELEGRAM_TOKEN environment variable not set"))
      _ <- new WineBot[IO](token).startPolling().map(_ => ExitCode.Success)
    } yield ExitCode.Success
  }
}
