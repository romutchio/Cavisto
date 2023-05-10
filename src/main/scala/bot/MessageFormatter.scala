package bot

import bot.domain.Commands
import bot.domain.states.{AdviseState, NoteState}
import vivino.domain.Wine


class MessageFormatter {

  private val commandList: String = {
    Commands.values.map(c => s"${c.command} — ${c.description}").mkString("\n")
  }

  val getHelpMessage: String = {
    s"""
       |Cavisto is a wine assistant, that can:
       |- advise wine, using filters
       |- search wine by piece of info you know
       |- save notes about wine
       |
       |_The commands are:_
       |$commandList
       |""".stripMargin
  }

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
       |$commandList
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
       |${adviseStateView(adviseState)}
       |""".stripMargin

  def getAdviseStateMessageWinesNotFound(adviseState: AdviseState): String =
    s"""
       |*Nothing was found* for selected options:
       |
       |${adviseStateView(adviseState)}
       |""".stripMargin

  def getNoteStateMessage(noteState: NoteState): String =
    s"""
       |Save your personal note about wine:
       |
       |${noteStateView(noteState)}
       |""".stripMargin

  private def adviseStateView(adviseState: AdviseState): String =
    s"""
       |*Country:* ${adviseState.country.getOrElse("...")}
       |*Type:* ${adviseState.wineType.getOrElse("...")}
       |*Price from:* ${adviseState.priceMin.map(price => s"€ $price").getOrElse("...")}
       |*Price to:* ${adviseState.priceMax.map(price => s"€ $price").getOrElse("...")}
       |""".stripMargin

  private def noteStateView(noteState: NoteState): String =
    s"""
       |*Wine name*: ${noteState.wineName.getOrElse("...")}
       |*Rating*: ${noteState.rating.getOrElse("...")}
       |*Price*: ${noteState.price.getOrElse("...")}
       |*Review*: ${noteState.review.getOrElse("...")}
       |""".stripMargin

  val getEditWineNameMessage = "OK. Send me the name of Wine."

  val getEditReviewMessage = "OK. Send me the new 'Review' text."

  val getEditPriceMessage = "OK. Send me the new 'Price' in Euro. For example: 40"

  val getEditRatingMessage = "OK. Send me the new 'Rating' for 1 to 5. For example: 3"

  def getEditAppliedMessage(fieldName: String, text: String) = s"Ok. $fieldName was changed to: $text"

  def getNoteSavedMessage(wineName: String) = s"Note about wine '$wineName' was successfully saved."

  val getNoteSavedErrorMessage = "Note was not saved. Try again later."

  val getAdviseHistorySavedErrorMessage = "Advise history was not saved."

  val getUserSavedErrorMessage = "Error, saving user info. Try again later."

}

object MessageFormatter {
  def make: MessageFormatter = new MessageFormatter
}
