package client

import cats.effect.Async
import io.circe.Decoder
import org.http4s.circe.jsonOf
import org.http4s.{Headers, MediaType, Method, Request, Uri}
import org.http4s.client.{Client, JavaNetClientBuilder}
import org.http4s.headers.Accept

class Http4sHttpClient[F[_]: Async] extends HttpClient[F] {
  private val client: Client[F] = JavaNetClientBuilder[F].create
  def get(url: String, query: Map[String, String]): F[String] = {
    val r = Request[F](
      method = Method.GET,
      uri = Uri.unsafeFromString(url).withQueryParams(query)
    )
    client.fetchAs[String](r)
  }
  def getJson[T: Decoder](url: String, queryStr: Map[String, String], queryInt: Map[String, Int]): F[T] = {
    val r = Request[F](
      method = Method.GET,
      uri = Uri.unsafeFromString(url)
        .withQueryParams(queryStr)
        .withQueryParams(queryInt),
      headers = Headers(
        Accept(MediaType.application.json),
      )
    )
    client.fetchAs[T](r)(jsonOf[F, T])
  }
}
