package bot.domain.buttons

import com.bot4s.telegram.models.InlineKeyboardMarkup

object ButtonMarkups {
  val AdviseMarkup: InlineKeyboardMarkup = {
    InlineKeyboardMarkup(
      Seq(
        Seq(
          CountryButton.Selection.toInlineKeyboardButton,
          WineTypeButton.Selection.toInlineKeyboardButton,
        ),
        Seq(
          PriceButton.Selection(PriceButtonType.Min).toInlineKeyboardButton,
          PriceButton.Selection(PriceButtonType.Max).toInlineKeyboardButton,
        ),
        Seq(
          AdviseButton.Advise.toInlineKeyboardButton,
        )
      )
    )
  }

  val CountryMarkup: InlineKeyboardMarkup = {
    InlineKeyboardMarkup(
      Seq(
        Seq(
          CountryButton.Argentina.toInlineKeyboardButton,
          CountryButton.Australia.toInlineKeyboardButton,
          CountryButton.Austria.toInlineKeyboardButton
        ),
        Seq(
          CountryButton.Chile.toInlineKeyboardButton,
          CountryButton.France.toInlineKeyboardButton,
          CountryButton.Germany.toInlineKeyboardButton,
          CountryButton.Italy.toInlineKeyboardButton,
        ),
        Seq(
          CountryButton.Portugal.toInlineKeyboardButton,
          CountryButton.Russia.toInlineKeyboardButton,
          CountryButton.Spain.toInlineKeyboardButton,
          CountryButton.UnitedStates.toInlineKeyboardButton
        ),
        Seq(
          AdviseButton.Return.toInlineKeyboardButton,
          CountryButton.Clear.toInlineKeyboardButton
        )
      )
    )
  }

  val WineTypeMarkup: InlineKeyboardMarkup = {
    InlineKeyboardMarkup(
      Seq(
        Seq(
          WineTypeButton.Red.toInlineKeyboardButton,
          WineTypeButton.White.toInlineKeyboardButton,
          WineTypeButton.Sparkling.toInlineKeyboardButton,
        ),
        Seq(
          WineTypeButton.Rose.toInlineKeyboardButton,
          WineTypeButton.Dessert.toInlineKeyboardButton,
          WineTypeButton.Fortified.toInlineKeyboardButton,
        ),
        Seq(
          AdviseButton.Return.toInlineKeyboardButton,
          WineTypeButton.Clear.toInlineKeyboardButton
        )
      )
    )
  }

  val PriceMinMarkup: InlineKeyboardMarkup = priceMarkup(PriceButtonType.Min)
  val PriceMaxMarkup: InlineKeyboardMarkup = priceMarkup(PriceButtonType.Max)

  private def priceMarkup(priceButtonType: PriceButtonType): InlineKeyboardMarkup = {
    InlineKeyboardMarkup(
      Seq(
        Seq(
          PriceButton.Euro("2", priceButtonType).toInlineKeyboardButton,
          PriceButton.Euro("3", priceButtonType).toInlineKeyboardButton,
          PriceButton.Euro("5", priceButtonType).toInlineKeyboardButton,
          PriceButton.Euro("10", priceButtonType).toInlineKeyboardButton,
          PriceButton.Euro("25", priceButtonType).toInlineKeyboardButton,
        ),
        Seq(
          PriceButton.Euro("50", priceButtonType).toInlineKeyboardButton,
          PriceButton.Euro("100", priceButtonType).toInlineKeyboardButton,
          PriceButton.Euro("150", priceButtonType).toInlineKeyboardButton,
          PriceButton.Euro("200", priceButtonType).toInlineKeyboardButton,
          PriceButton.Euro("250", priceButtonType).toInlineKeyboardButton,
        ),
        Seq(
          PriceButton.Euro("300", priceButtonType).toInlineKeyboardButton,
          PriceButton.Euro("350", priceButtonType).toInlineKeyboardButton,
          PriceButton.Euro("400", priceButtonType).toInlineKeyboardButton,
          PriceButton.Euro("450", priceButtonType).toInlineKeyboardButton,
          PriceButton.Euro("500", priceButtonType).toInlineKeyboardButton,
        ),
        Seq(
          AdviseButton.Return.toInlineKeyboardButton,
          PriceButton.Clear(priceButtonType).toInlineKeyboardButton
        )
      )
    )
  }
}
