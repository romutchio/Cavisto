package mocks

import cats.effect.Sync
import client.HttpClient
import io.circe.Decoder
import io.circe.parser.decode

import scala.io.Source

class HttpClientMock[F[_]: Sync] extends HttpClient[F] {
  def readFile(filename: String): String = {
    val src = Source.fromResource(filename)
    src.mkString
  }
  def get(url: String, query: Map[String, String]): F[String] = {
    if (url == "https://www.vivino.com/search/wines") {
      Sync[F].delay(readFile("vivino_search_mock.html"))
    } else {
      Sync[F].delay("")
    }
  }

  def getJson[T: Decoder](url: String, query: Map[String, String]): F[T] = {
    if (url == "https://www.vivino.com/api/explore/explore") {
      Sync[F].pure(
        decode[T](readFile("vivino_explore_mock.json")).toOption.get
      )
    } else {
      Sync[F].pure(decode[T]("{}").toOption.get)
    }
  }
}
