package vivino.parser

import cats.effect.Sync
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.scraper.ContentExtractors.{attr, element, elementList, text}
import vivino.domain.Wine

class JsoupVivinoHtmlParser[F[_] : Sync] extends VivinoHtmlParser[F] {
  private val jsoupBrowser = JsoupBrowser()

  def parseSearchHtml(html: String): F[List[Wine]] = Sync[F].delay {
    val doc = jsoupBrowser.parseString(html)
    val wineCards = doc >> elementList(".card")
    wineCards.map { wineCard =>
      val name = wineCard >> text(".wine-card__name")
      val rating = (wineCard >> text(".average__number")).replace(",", ".").toDoubleOption
      val pricePrefix: String = wineCard >> text(".wine-price-prefix")
      val priceSuffix: String = wineCard >> text(".wine-price-suffix")
      val priceValue = (wineCard >> text(".wine-price-value")).replace(",", ".").toDoubleOption
      val urlRegex = "url\\((.*?)\\)".r
      val imageStyle = wineCard >> element(".wine-card__image") >> attr("style")
      val imageUrl = urlRegex.findFirstMatchIn(imageStyle) match {
        case Some(matched) => matched.group(1)
        case None => ""
      }
      val url = imageUrl match {
        case s"//${url}" => Some(url)
        case x if x.startsWith("http") || x.nonEmpty => Some(x)
        case _ => None
      }
      val price: Option[String] = (pricePrefix, priceSuffix, priceValue) match {
        case (_, _, None) => None
        case (prefix, "", Some(value)) => Some(s"$prefix $value")
        case ("", suffix, Some(value)) => Some(s"$value $suffix")
        case _ => None
      }

      Wine(name, rating, price, url)
    }
  }
}

object JsoupVivinoHtmlParser {
  def make[F[_] : Sync]: F[JsoupVivinoHtmlParser[F]] = Sync[F].delay(new JsoupVivinoHtmlParser[F])
}
