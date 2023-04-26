package vivino.domain

trait WineType {
  val id: Int
}

object WineType {
  final case object Red extends WineType {
    val id: Int = 1
  }

  final case object White extends WineType {
    val id: Int = 2
  }

  final case object Sparkling extends WineType {
    val id: Int = 3
  }

  final case object Rose extends WineType {
    val id: Int = 4
  }

  final case object Dessert extends WineType {
    val id: Int = 7
  }

  final case object Fortified extends WineType {
    val id: Int = 24
  }

}
