package client

import io.circe.Decoder

trait HttpClient[F[_]] {
  def get(url: String, query: Map[String, String]): F[String]
  def getJson[T : Decoder](url: String, query: Map[String, String]): F[T]
}
