package bot

import bot.domain.AdviseState
import vivino.domain.Wine


class MessageFormatter {
  def getWelcomeMessage(username: Option[String], firstName: String, lastName: Option[String]): String = {
    val name = (username, firstName, lastName) match {
      case (Some(user), _, _) => user
      case (_, firstName, Some(last)) => s"$firstName $last"
      case (_, firstName, _) => firstName
    }
    s"""
      |*$name*, welcome to Cavisto bot.
      |
      |_Available commands:_
      |/advise -- Ask for wine advice, using filters
      |/search -- Search wines by name. Example: /search cabernet
      |""".stripMargin
  }

  def getSearchWinesMessage(input: String): String = {
    s"Searching for: _${input}_"
  }

  def getWinesFoundMessage(wines: List[Wine]): String = wines.map(getWineMessage).mkString("\n")

  private def getWineMessage(wine: Wine): String = {
    val price = wine.price.getOrElse("...")
    val rating = wine.rating.map(_.toString).getOrElse("...")
    s"*${wine.name}* _Price: ${price}_ _Rating: ${rating}_"
  }

  def getWinesNotFoundMessage(input: String): String = {
    s"Nothing found for: _${input}_"
  }

  def getAdviseStateMessage(adviseState: AdviseState): String =
    s"""
       |Advise options:
       |
       |*Country:* ${adviseState.country.getOrElse("-")}
       |*Type:* ${adviseState.wineType.getOrElse("-")}
       |*Price from:* ${adviseState.priceMin.map(price => s"€ $price").getOrElse("-")}
       |*Price to:* ${adviseState.priceMax.map(price => s"€ $price").getOrElse("-")}
       |""".stripMargin

  def getAdviseStateMessageWinesNotFound(adviseState: AdviseState): String =
    s"""
       |*Nothing was found* for selected options:
       |
       |*Country:* ${adviseState.country.getOrElse("-")}
       |*Type:* ${adviseState.wineType.getOrElse("-")}
       |*Price from:* ${adviseState.priceMin.map(price => s"€ $price").getOrElse("-")}
       |*Price to:* ${adviseState.priceMax.map(price => s"€ $price").getOrElse("-")}
       |""".stripMargin
}

object MessageFormatter {
  def make: MessageFormatter = new MessageFormatter
}
