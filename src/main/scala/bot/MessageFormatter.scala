package bot

import bot.domain.AdviseState
import vivino.domain.Wine


class MessageFormatter {
  def winesSearch(input: String): String = {
    s"Searching for: _${input}_"
  }

  def winesToMessage(wines: List[Wine]): String = wines.map(wineToMessage).mkString("\n")

  private def wineToMessage(wine: Wine): String = List(
    Some(s"*${wine.name}*"),
    wine.price.map(x => s"_Price: ${x}_"),
    wine.rating.map(x => s"_Rating: ${x}_")
  ).flatten.mkString(" ")

  def winesToMessageNotFound(input: String): String = {
    s"Nothing found for: _${input}_"
  }

  def adviseStateToMessage(adviseState: AdviseState): String = {
    List(
      Some("Advice options:"),
      Some(""),
      adviseState.country.map(x => s"*Country:* $x"),
      adviseState.wineType.map(x => s"*Type:* $x"),
      adviseState.priceMin.map(x => s"*Price from:* € $x"),
      adviseState.priceMax.map(x => s"*Price to:* € $x")
    ).flatten.mkString("\n")
  }

  def adviseStateToMessageNotFound(adviseState: AdviseState): String = {
    List(
      Some("*Nothing was found* for selected options:"),
      Some(""),
      adviseState.country.map(x => s"*Country:* $x"),
      adviseState.wineType.map(x => s"*Type:* $x"),
      adviseState.priceMin.map(x => s"*Price from:* € $x"),
      adviseState.priceMax.map(x => s"*Price to:* € $x")
    ).mkString("\n")
  }
}
