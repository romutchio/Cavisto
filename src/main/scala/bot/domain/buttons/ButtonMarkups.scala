package bot.domain.buttons

import bot.domain.buttons.AdviseButtons._
import bot.domain.buttons.CountryButtons._
import bot.domain.buttons.PriceButtons._
import bot.domain.buttons.WineTypeButtons._
import com.bot4s.telegram.models.InlineKeyboardMarkup

object ButtonMarkups {
  val AdviseMarkup: InlineKeyboardMarkup = {
    InlineKeyboardMarkup(
      Seq(
        Seq(
          CountrySelectionButton.toInlineKeyboardButton,
          WineTypeSelectionButton.toInlineKeyboardButton,
        ),
        Seq(
          PriceSelectionButton(PriceButtonType.Min).toInlineKeyboardButton,
          PriceSelectionButton(PriceButtonType.Max).toInlineKeyboardButton,
        ),
        Seq(
          AdviseWineButton.toInlineKeyboardButton,
        )
      )
    )
  }

  val CountryMarkup: InlineKeyboardMarkup = {
    InlineKeyboardMarkup(
      Seq(
        Seq(
          ArgentinaCountryButton.toInlineKeyboardButton,
          AustraliaCountryButton.toInlineKeyboardButton,
          AustriaCountryButton.toInlineKeyboardButton
        ),
        Seq(
          ChileCountryButton.toInlineKeyboardButton,
          FranceCountryButton.toInlineKeyboardButton,
          GermanyCountryButton.toInlineKeyboardButton,
          ItalyCountryButton.toInlineKeyboardButton,
        ),
        Seq(
          PortugalCountryButton.toInlineKeyboardButton,
          RussiaCountryButton.toInlineKeyboardButton,
          SpainCountryButton.toInlineKeyboardButton,
          UnitedStatesCountryButton.toInlineKeyboardButton
        ),
        Seq(
          ReturnToSelectionButton.toInlineKeyboardButton,
          ClearCountrySelectionButton.toInlineKeyboardButton
        )
      )
    )
  }

  val WineTypeMarkup: InlineKeyboardMarkup = {
    InlineKeyboardMarkup(
      Seq(
        Seq(
          RedWineButton.toInlineKeyboardButton,
          WhiteWineButton.toInlineKeyboardButton,
          SparklingWineButton.toInlineKeyboardButton,
        ),
        Seq(
          RoseWineButton.toInlineKeyboardButton,
          DessertWineButton.toInlineKeyboardButton,
          FortifiedWineButton.toInlineKeyboardButton,
        ),
        Seq(
          ReturnToSelectionButton.toInlineKeyboardButton,
          ClearWineTypeSelectionButton.toInlineKeyboardButton
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
          EuroPriceButton("2", priceButtonType).toInlineKeyboardButton,
          EuroPriceButton("3", priceButtonType).toInlineKeyboardButton,
          EuroPriceButton("5", priceButtonType).toInlineKeyboardButton,
          EuroPriceButton("10", priceButtonType).toInlineKeyboardButton,
          EuroPriceButton("25", priceButtonType).toInlineKeyboardButton,
        ),
        Seq(
          EuroPriceButton("50", priceButtonType).toInlineKeyboardButton,
          EuroPriceButton("100", priceButtonType).toInlineKeyboardButton,
          EuroPriceButton("150", priceButtonType).toInlineKeyboardButton,
          EuroPriceButton("200", priceButtonType).toInlineKeyboardButton,
          EuroPriceButton("250", priceButtonType).toInlineKeyboardButton,
        ),
        Seq(
          EuroPriceButton("300", priceButtonType).toInlineKeyboardButton,
          EuroPriceButton("350", priceButtonType).toInlineKeyboardButton,
          EuroPriceButton("400", priceButtonType).toInlineKeyboardButton,
          EuroPriceButton("450", priceButtonType).toInlineKeyboardButton,
          EuroPriceButton("500", priceButtonType).toInlineKeyboardButton,
        ),
        Seq(
          ReturnToSelectionButton.toInlineKeyboardButton,
          ClearPriceSelectionButton(priceButtonType).toInlineKeyboardButton
        )
      )
    )
  }
}
