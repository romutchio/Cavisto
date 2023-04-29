package bot.domain.buttons

import com.bot4s.telegram.models.InlineKeyboardMarkup

object ButtonMarkup {
  val AdviseMarkup: InlineKeyboardMarkup = {
    InlineKeyboardMarkup(
      Seq(
        Seq(
          CountryButton.Selection, WineTypeButton.Selection
        ),
        Seq(
          PriceButton.Selection(PriceButtonType.Min), PriceButton.Selection(PriceButtonType.Max)
        ),
        Seq(
          AdviseButton.Advise
        ),
      ).map(_.map(_.toKeyboardButton))
    )
  }

  val CountryMarkup: InlineKeyboardMarkup = {
    InlineKeyboardMarkup(
      Seq(
        Seq(
          CountryButton.Argentina, CountryButton.Australia, CountryButton.Austria
        ),
        Seq(
          CountryButton.Chile,     CountryButton.France,    CountryButton.Germany, CountryButton.Italy,
        ),
        Seq(
          CountryButton.Portugal,  CountryButton.Russia,    CountryButton.Spain,   CountryButton.UnitedStates
        ),
        Seq(
          AdviseButton.Return,     CountryButton.Clear
        ),
      ).map(_.map(_.toKeyboardButton))
    )
  }

  val WineTypeMarkup: InlineKeyboardMarkup = {
    InlineKeyboardMarkup(
      Seq(
        Seq(
          WineTypeButton.Red,  WineTypeButton.White,   WineTypeButton.Sparkling,
        ),
        Seq(
          WineTypeButton.Rose, WineTypeButton.Dessert, WineTypeButton.Fortified,
        ),
        Seq(
          AdviseButton.Return, WineTypeButton.Clear
        ),
      ).map(_.map(_.toKeyboardButton))
    )
  }

  val PriceMinMarkup: InlineKeyboardMarkup = priceMarkup(PriceButtonType.Min)
  val PriceMaxMarkup: InlineKeyboardMarkup = priceMarkup(PriceButtonType.Max)

  private def priceMarkup(priceButtonType: PriceButtonType): InlineKeyboardMarkup = {
    InlineKeyboardMarkup(
      Seq(
        Seq(
          PriceButton.Euro("2", priceButtonType),
          PriceButton.Euro("3", priceButtonType),
          PriceButton.Euro("5", priceButtonType),
          PriceButton.Euro("10", priceButtonType),
          PriceButton.Euro("25", priceButtonType),
        ),
        Seq(
          PriceButton.Euro("50", priceButtonType),
          PriceButton.Euro("100", priceButtonType),
          PriceButton.Euro("150", priceButtonType),
          PriceButton.Euro("200", priceButtonType),
          PriceButton.Euro("250", priceButtonType),
        ),
        Seq(
          PriceButton.Euro("300", priceButtonType),
          PriceButton.Euro("350", priceButtonType),
          PriceButton.Euro("400", priceButtonType),
          PriceButton.Euro("450", priceButtonType),
          PriceButton.Euro("500", priceButtonType),
        ),
        Seq(
          AdviseButton.Return, PriceButton.Clear(priceButtonType)
        ),
      ).map(_.map(_.toKeyboardButton))
    )
  }

  val NoteMarkup: InlineKeyboardMarkup = {
    InlineKeyboardMarkup(
      Seq(
        Seq(EditNoteButton.Name, EditNoteButton.Rating),
        Seq(EditNoteButton.Price, EditNoteButton.Review),
        Seq(NoteButton.Return, NoteButton.Save),
      ).map(_.map(_.toKeyboardButton))
    )
  }
}
