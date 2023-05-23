  package vivino.domain

sealed trait FoodPairing {
  val id: Int
}

object FoodPairing {
  final case object AnyJunkFood extends FoodPairing {
    val id: Int = 21
  }

  final case object Aperitif extends FoodPairing {
    val id: Int = 40
  }

  final case object AppetizersSnacks extends FoodPairing {
    val id: Int = 27
  }

  final case object Beef extends FoodPairing {
    val id: Int = 4
  }

  final case object CuredMeat extends FoodPairing {
    val id: Int = 41
  }

  final case object FruityDesserts extends FoodPairing {
    val id: Int = 37
  }

  final case object Lamb extends FoodPairing {
    val id: Int = 8
  }

  final case object LeanFish extends FoodPairing {
    val id: Int = 28
  }

  final case object MatureAndHardCheese extends FoodPairing {
    val id: Int = 17
  }

  final case object MildAndSoftCheese extends FoodPairing {
    val id: Int = 35
  }

  final case object Mushrooms extends FoodPairing {
    val id: Int = 34
  }

  final case object Pasta extends FoodPairing {
    val id: Int = 5
  }

  final case object Pork extends FoodPairing {
    val id: Int = 10
  }

  final case object Poultry extends FoodPairing {
    val id: Int = 20
  }

  final case object RichFish extends FoodPairing {
    val id: Int = 12
  }

  final case object Shellfish extends FoodPairing {
    val id: Int = 13
  }

  final case object SpicyFood extends FoodPairing {
    val id: Int = 15
  }

  final case object SweetDesserts extends FoodPairing {
    val id: Int = 16
  }

  final case object Vegetarian extends FoodPairing {
    val id: Int = 19
  }

}
