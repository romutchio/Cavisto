package bot.domain.buttons

import com.bot4s.telegram.models.InlineKeyboardMarkup

object ButtonMarkup {
  val AdviseMarkup: InlineKeyboardMarkup = {
    InlineKeyboardMarkup(
      Seq(
        Seq(
          CountryButton.Selection, WineTypeButton.Selection
        ).map(_.toKeyboardButton),
        Seq(
          PriceButton.Selection(PriceButtonType.Min), PriceButton.Selection(PriceButtonType.Max)
        ).map(_.toKeyboardButton),
        Seq(
          AdviseButton.Advise
        ).map(_.toKeyboardButton),
      )
    )
  }

  val CountryMarkup: InlineKeyboardMarkup = {
    InlineKeyboardMarkup(
      Seq(
        Seq(
          CountryButton.Argentina, CountryButton.Australia, CountryButton.Austria
        ).map(_.toKeyboardButton),
        Seq(
          CountryButton.Chile,     CountryButton.France,    CountryButton.Germany, CountryButton.Italy,
        ).map(_.toKeyboardButton),
        Seq(
          CountryButton.Portugal,  CountryButton.Russia,    CountryButton.Spain,   CountryButton.UnitedStates
        ).map(_.toKeyboardButton),
        Seq(
          AdviseButton.Return,     CountryButton.Clear
        ).map(_.toKeyboardButton)
      )
    )
  }

  val WineTypeMarkup: InlineKeyboardMarkup = {
    InlineKeyboardMarkup(
      Seq(
        Seq(
          WineTypeButton.Red,  WineTypeButton.White,   WineTypeButton.Sparkling,
        ).map(_.toKeyboardButton),
        Seq(
          WineTypeButton.Rose, WineTypeButton.Dessert, WineTypeButton.Fortified,
        ).map(_.toKeyboardButton),
        Seq(
          AdviseButton.Return, WineTypeButton.Clear
        ).map(_.toKeyboardButton)
      )
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
        ).map(_.toKeyboardButton),
        Seq(
          PriceButton.Euro("50", priceButtonType),
          PriceButton.Euro("100", priceButtonType),
          PriceButton.Euro("150", priceButtonType),
          PriceButton.Euro("200", priceButtonType),
          PriceButton.Euro("250", priceButtonType),
        ).map(_.toKeyboardButton),
        Seq(
          PriceButton.Euro("300", priceButtonType),
          PriceButton.Euro("350", priceButtonType),
          PriceButton.Euro("400", priceButtonType),
          PriceButton.Euro("450", priceButtonType),
          PriceButton.Euro("500", priceButtonType),
        ).map(_.toKeyboardButton),
        Seq(
          AdviseButton.Return, PriceButton.Clear(priceButtonType)
        ).map(_.toKeyboardButton)
      )
    )
  }
}
