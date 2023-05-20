package bot.domain

import bot.domain.buttons._
import com.bot4s.telegram.models.InlineKeyboardMarkup
import database.domain.Note

object ButtonMarkup {
  val AdviseMarkup: InlineKeyboardMarkup = {
    InlineKeyboardMarkup(
      Seq(
        Seq(CountryButton.Selection, WineTypeButton.Selection),
        Seq(PriceButton.Selection(PriceButtonType.Min), PriceButton.Selection(PriceButtonType.Max)),
        Seq(AdviseButton.Clear, AdviseButton.Advise),
      ).map(_.map(_.toKeyboardButton))
    )
  }

  val CountryMarkup: InlineKeyboardMarkup = {
    InlineKeyboardMarkup(
      Seq(
        Seq(CountryButton.Argentina, CountryButton.Australia, CountryButton.Austria),
        Seq(CountryButton.Chile, CountryButton.France, CountryButton.Germany, CountryButton.Italy),
        Seq(CountryButton.Portugal, CountryButton.Russia, CountryButton.Spain, CountryButton.UnitedStates),
        Seq(AdviseButton.Return, CountryButton.Clear),
      ).map(_.map(_.toKeyboardButton))
    )
  }

  val WineTypeMarkup: InlineKeyboardMarkup = {
    InlineKeyboardMarkup(
      Seq(
        Seq(WineTypeButton.Red, WineTypeButton.White, WineTypeButton.Sparkling),
        Seq(WineTypeButton.Rose, WineTypeButton.Dessert, WineTypeButton.Fortified),
        Seq(AdviseButton.Return, WineTypeButton.Clear),
      ).map(_.map(_.toKeyboardButton))
    )
  }

  val PriceMinMarkup: InlineKeyboardMarkup = priceMarkup(PriceButtonType.Min)
  val PriceMaxMarkup: InlineKeyboardMarkup = priceMarkup(PriceButtonType.Max)

  private def priceMarkup(priceButtonType: PriceButtonType): InlineKeyboardMarkup = {
    InlineKeyboardMarkup(
      Seq(
        Seq(2, 3, 5, 10, 25).map(PriceButton.Euro(_, priceButtonType)),
        Seq(50, 100, 150, 200, 250).map(PriceButton.Euro(_, priceButtonType)),
        Seq(300, 350, 400, 450, 500).map(PriceButton.Euro(_, priceButtonType)),
        Seq(AdviseButton.Return, PriceButton.Clear(priceButtonType)),
      ).map(_.map(_.toKeyboardButton))
    )
  }

  val NoteMarkup: InlineKeyboardMarkup = {
    InlineKeyboardMarkup(
      Seq(
        Seq(EditNoteButton.Name, EditNoteButton.Rating),
        Seq(EditNoteButton.Price, EditNoteButton.Review),
        Seq(NoteButton.Return, NoteButton.Clear),
        Seq(NoteButton.Save),
      ).map(_.map(_.toKeyboardButton))
    )
  }

  val SearchNavigationMarkup: InlineKeyboardMarkup = {
    InlineKeyboardMarkup(
      Seq(
        Seq(SearchButton.Previous, SearchButton.Next),
      ).map(_.map(_.toKeyboardButton))
    )
  }

  val AdviseNavigationMarkup: InlineKeyboardMarkup = {
    InlineKeyboardMarkup(
      Seq(
        Seq(AdviseButton.Previous, AdviseButton.Next),
      ).map(_.map(_.toKeyboardButton))
    )
  }

  def NotesMarkup(notes: List[Note], page: Int, notesCount: Int): InlineKeyboardMarkup = {
    val startWith = page * notesCount
    val halfNotesCount = notesCount / 2
    val buttons: (List[(Note, Int)], List[(Note, Int)]) = notes match {
      case x if x.length > halfNotesCount
      => notes.zip(LazyList.from(startWith)).partition { case (_, i) => i < halfNotesCount }
      case _ => (Nil, notes.zip(LazyList.from(startWith)))
    }
    InlineKeyboardMarkup(
      Seq(
        buttons._1.map { case (_, idx) => NotesButtons.NoteId(idx) },
        buttons._2.map { case (_, idx) => NotesButtons.NoteId(idx) },
        Seq(NotesButtons.Previous, NotesButtons.Next),
      ).map(_.map(_.toKeyboardButton))
    )
  }
}
