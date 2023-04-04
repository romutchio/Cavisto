package parser

import cats.effect.Async
import domain.Wine
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.scraper.ContentExtractors.{elementList, text}
import net.ruippeixotog.scalascraper.dsl.DSL._

class JsoupVivinoHtmlParser[F[_]: Async] extends VivinoHtmlParser[F] {
  private val jsoupBrowser = JsoupBrowser()

  def parseSearchHtml(html: String): F[List[Wine]] = Async[F].delay {
    val doc = jsoupBrowser.parseString(html)
    val wineCards = doc >> elementList(".wine-card__content")
    wineCards.map { wineCard =>
      val name = wineCard >> text(".wine-card__name")
      val rating = (wineCard >> text(".average__number")).replace(",", ".").toDoubleOption
      val price: Option[String] = wineCard >> text(".wine-price-value") match {
        case "—" | "" => None
        case x => Some(x)
      }

      Wine(name, rating, price)
    }
  }
}
