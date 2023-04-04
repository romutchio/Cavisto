import cats.effect.{ExitCode, IO, IOApp}
import client.Http4sHttpClient
import domain.CountryCode.France
import domain.CurrencyCode.Euro
import parser.JsoupVivinoHtmlParser
import vivino.VivinoWineClient


object Main extends IOApp {
  val run: IO[Unit] = {
    implicit val jsoupVivinoHTMLParser: JsoupVivinoHtmlParser[IO] = new JsoupVivinoHtmlParser[IO]()
    implicit val httpClient: Http4sHttpClient[IO] = new Http4sHttpClient[IO]()
    val vivinoClient = new VivinoWineClient[IO]()

    for {
      resp1 <- vivinoClient.adviseWine(France, Euro, 3, 4)
      resp2 <- vivinoClient.getWinesByName("baron d'")
      _ = println(resp1)
      _ = println(resp2)
    } yield ()

  }

  override def run(args: List[String]): IO[ExitCode] = {
    for {
      _ <- run
    } yield ExitCode.Success
  }
}
