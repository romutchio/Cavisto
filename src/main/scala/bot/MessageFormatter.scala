package bot

import bot.domain.Command
import bot.domain.states.{AdviseState, NoteState}
import database.domain.Note
import vivino.domain.Wine


class MessageFormatter {

  private val commandList: String = {
    Command.values.map(c => s"${c.command} — ${c.description}").mkString("\n")
  }

  val getHelpMessage: String = {
    s"""Cavisto is a wine assistant, that can:
       |- advise wine, using filters
       |- search wine by piece of info you know
       |- save notes about wine
       |
       |_The commands are:_
       |$commandList""".stripMargin
  }
  def getWelcomeMessage(username: Option[String], firstName: String, lastName: Option[String]): String = {
    val name = (username, firstName, lastName) match {
      case (Some(user), _, _) => user
      case (_, firstName, Some(last)) => s"$firstName $last"
      case (_, firstName, _) => firstName
    }
    s"""*$name*, welcome to Cavisto bot.
       |
       |_Available commands:_
       |$commandList""".stripMargin
  }

  def getSearchWinesMessage(input: String): String = {
    s"Searching for: _${input}_"
  }

  def getWineMessage(wine: Wine, wineId: Int, winesCount: Int): String = {
    val price = wine.price.getOrElse("...")
    val rating = wine.rating.map(_.toString).getOrElse("...")
    val imageUrl = wine.url.map(url => s"[​]($url)").getOrElse("")

    s"""Found $winesCount wines:
      |$imageUrl
      |$wineId. *${wine.name}*
      |_Price: ${price}_
      |_Rating: ${rating}_""".stripMargin
  }

  def getWinesNotFoundMessage(input: String): String = {
    s"Nothing found for: _${input}_"
  }

  def getAdviseStateMessage(adviseState: AdviseState): String =
    s"""Advise options:
       |
       |${adviseStateView(adviseState)}""".stripMargin

  def getAdviseStateMessageWinesNotFound(adviseState: AdviseState): String =
    s"""*Nothing was found* for selected options:
       |
       |${adviseStateView(adviseState)}""".stripMargin

  def getNoteStateMessage(noteState: NoteState): String =
    s"""Save your personal note about wine:
       |
       |${noteStateView(noteState)}""".stripMargin

  def adviseStateView(adviseState: AdviseState): String =
    s"""*Country:* ${adviseState.country.getOrElse("...")}
       |*Type:* ${adviseState.wineType.getOrElse("...")}
       |*Price from:* ${adviseState.priceMin.map(price => s"€ $price").getOrElse("...")}
       |*Price to:* ${adviseState.priceMax.map(price => s"€ $price").getOrElse("...")}""".stripMargin

  def noteStateView(noteState: NoteState): String =
    s"""*Wine name*: ${noteState.wineName.getOrElse("...")}
       |*Rating*: ${noteState.rating.getOrElse("...")}
       |*Price*: ${noteState.price.map(price => s"€ $price").getOrElse("...")}
       |*Review*: ${noteState.review.getOrElse("...")}""".stripMargin

  def noteView(idx: Int, note: Note): String =
    s"$idx. " +
      s"*Name*: ${note.wine_name.getOrElse("...")}, " +
      s"*rating*: ${note.rating.getOrElse("...")}, " +
      s"*price*: ${note.price.map(price => s"€ $price").getOrElse("...")} "

  def notesList(notes: List[Note], startWith: Int): String =
    notes.zipWithIndex.map {case (note, id) => noteView(startWith + id, note)}.mkString("\n")
  def getNotesList(notes: List[Note], pageNumber: Int, onPage: Int = 10, total: Int): String = {
    val startWith = pageNumber * onPage
    s"""*Personal notes*
       |
       |${if (notes.nonEmpty) notesList(notes, startWith) else "..."}
       |
       |_Total:_ $total, _Current page:_ $pageNumber""".stripMargin
  }

  val getEditWineNameMessage = "OK. Send me the name of Wine."

  val getEditReviewMessage = "OK. Send me the new 'Review' text."

  val getEditPriceMessage = "OK. Send me the new 'Price' in Euro. For example: 40"

  val getEditRatingMessage = "OK. Send me the new 'Rating' for 1 to 5. For example: 3"

  def getEditAppliedMessage(fieldName: String, text: String) = s"Ok. $fieldName was changed to: $text"

  def getNoteSavedMessage(wineName: String) = s"Note about wine '$wineName' was successfully saved."

  val getNoteSavedErrorMessage = "Note was not saved. Try again later."

  val getAdviseHistorySavedErrorMessage = "Advise history was not saved."

  val getUserSavedErrorMessage = "Error, saving user info. Try again later."

  val getNotesErrorMessage = "Error, getting notes. Try again later."

  val getNotesOutOfBoundaryErrorMessage = "No more notes to display."

  val getWinesOutOfBoundaryErrorMessage = "No more wines to display."

}

object MessageFormatter {
  def make: MessageFormatter = new MessageFormatter
}
