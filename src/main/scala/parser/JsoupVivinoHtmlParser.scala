package parser

import cats.effect.Sync
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.scraper.ContentExtractors.{elementList, text}
import vivino.domain.Wine

class JsoupVivinoHtmlParser[F[_] : Sync] extends VivinoHtmlParser[F] {
  private val jsoupBrowser = JsoupBrowser()

  def parseSearchHtml(html: String): F[List[Wine]] = Sync[F].delay {
    val doc = jsoupBrowser.parseString(html)
    val wineCards = doc >> elementList(".wine-card__content")
    wineCards.map { wineCard =>
      val name = wineCard >> text(".wine-card__name")
      val rating = (wineCard >> text(".average__number")).replace(",", ".").toDoubleOption
      val pricePrefix: String = wineCard >> text(".wine-price-prefix")
      val priceSuffix: String = wineCard >> text(".wine-price-suffix")
      val priceValue = (wineCard >> text(".wine-price-value")).replace(",", ".").toDoubleOption
      val price: Option[String] = (pricePrefix, priceSuffix, priceValue) match {
        case (_, _, None) => None
        case (prefix, "", Some(value)) => Some(s"$prefix $value")
        case ("", suffix, Some(value)) => Some(s"$value $suffix")
        case _ => None
      }

      Wine(name, rating, price)
    }
  }
}
