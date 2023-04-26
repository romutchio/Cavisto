package parser

import vivino.domain.Wine

trait VivinoHtmlParser[F[_]] {
  def parseSearchHtml(html: String): F[List[Wine]]
}
