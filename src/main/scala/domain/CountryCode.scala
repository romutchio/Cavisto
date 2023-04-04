package domain

sealed trait CountryCode {
  val code: String
}

object CountryCode {
  final case object France extends CountryCode {
    val code: String = "FR"
  }
}
