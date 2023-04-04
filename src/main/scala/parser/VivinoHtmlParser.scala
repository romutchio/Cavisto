package parser

import domain.Wine

trait VivinoHtmlParser[F[_]] {
  def parseSearchHtml(html: String): F[List[Wine]]
}
