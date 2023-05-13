package mocks

import cats.effect.Sync
import client.HttpClient
import io.circe.Decoder
import io.circe.parser.decode


object HttpClientMock {
  def test[F[_]: Sync](pf: PartialFunction[(String, Map[String, String]), String]): HttpClient[F] = new HttpClient[F] {
    def get(url: String, query: Map[String, String]): F[String] = Sync[F].delay(pf((url, query)))

    def getJson[T: Decoder](url: String, query: Map[String, String]): F[T] = Sync[F].delay(
      decode[T](pf((url, query))).toOption.get
    )
  }
}
