package vivino.domain

trait GrapeType {
  val id: Int
}

object GrapeType {
  final case object CabernetFranc extends GrapeType {
    val id: Int = 3
  }

  final case object CabernetSauvignon extends GrapeType {
    val id: Int = 2
  }

  final case object Chardonnay extends GrapeType {
    val id: Int = 5
  }

  final case object Grenache extends GrapeType {
    val id: Int = 8
  }

  final case object Malbec extends GrapeType {
    val id: Int = 9
  }

  final case object Merlot extends GrapeType {
    val id: Int = 10
  }

  final case object PinotNoir extends GrapeType {
    val id: Int = 14
  }

  final case object Riesling extends GrapeType {
    val id: Int = 15
  }

  final case object SauvignonBlanc extends GrapeType {
    val id: Int = 17
  }

  final case object Shiraz extends GrapeType {
    val id: Int = 1
  }
}