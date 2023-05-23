package bot.domain.buttons

import bot.domain.Button
import enumeratum._
import vivino.domain.GrapeType

abstract class GrapeTypeButton extends Button {
  val tag: String = "GrapeType_TAG"
}

sealed abstract class Grape(val grapeType: GrapeType)
  extends GrapeTypeButton with EnumEntry

object GrapeTypeButton extends Enum[Grape] {
  val values: IndexedSeq[Grape] = findValues

  final case object Selection extends GrapeTypeButton {
    val emoji: Option[String] = Some("\uD83C\uDF47")
    val name: String = "Grape"
  }

  final case object Clear extends GrapeTypeButton {
    val emoji: Option[String] = None
    val name: String = "Clear"
  }

  final case object CabernetFranc extends Grape(GrapeType.CabernetFranc) {
    val emoji: Option[Nothing] = None
    val name = "Cabernet Franc"
  }

  final case object CabernetSauvignon extends Grape(GrapeType.CabernetSauvignon) {
    val emoji: Option[Nothing] = None
    val name = "Cabernet Sauvignon"
  }

  final case object Chardonnay extends Grape(GrapeType.Chardonnay) {
    val emoji: Option[Nothing] = None
    val name = "Chardonnay"
  }

  final case object Grenache extends Grape(GrapeType.Grenache) {
    val emoji: Option[Nothing] = None
    val name = "Grenache"
  }

  final case object Malbec extends Grape(GrapeType.Malbec) {
    val emoji: Option[Nothing] = None
    val name = "Malbec"
  }

  final case object Merlot extends Grape(GrapeType.Merlot) {
    val emoji: Option[Nothing] = None
    val name = "Merlot"
  }

  final case object PinotNoir extends Grape(GrapeType.PinotNoir) {
    val emoji: Option[Nothing] = None
    val name = "Pinot Noir"
  }

  final case object Riesling extends Grape(GrapeType.Riesling) {
    val emoji: Option[Nothing] = None
    val name = "Riesling"
  }

  final case object SauvignonBlanc extends Grape(GrapeType.SauvignonBlanc) {
    val emoji: Option[Nothing] = None
    val name = "Sauvignon Blanc"
  }

  final case object Shiraz extends Grape(GrapeType.Shiraz) {
    val emoji: Option[Nothing] = None
    val name = "Shiraz"
  }

}
