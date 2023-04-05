package domain

sealed trait CurrencyCode {
  val code: String
}

object CurrencyCode {
  final case object Euro extends CurrencyCode {
    val code: String = "EUR"
  }
}

