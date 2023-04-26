package vivino.domain

sealed trait CountryCode {
  val code: String
}

object CountryCode {
  final case object France extends CountryCode {
    val code: String = "fr"
  }

  final case object Argentina extends CountryCode {
    val code: String = "ar"
  }

  final case object Australia extends CountryCode {
    val code: String = "au"
  }

  final case object Austria extends CountryCode {
    val code: String = "at"
  }

  final case object Chile extends CountryCode {
    val code: String = "cl"
  }

  final case object Germany extends CountryCode {
    val code: String = "de"
  }

  final case object Italy extends CountryCode {
    val code: String = "it"
  }

  final case object Portugal extends CountryCode {
    val code: String = "pt"
  }

  final case object Russia extends CountryCode {
    val code: String = "ru"
  }

  final case object Spain extends CountryCode {
    val code: String = "es"
  }

  final case object UnitedStates extends CountryCode {
    val code: String = "us"
  }
}
