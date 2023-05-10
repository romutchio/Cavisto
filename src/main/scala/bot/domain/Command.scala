package bot.domain
import enumeratum._

import scala.util.matching.Regex

sealed abstract class Command extends EnumEntry {
  val command: String
  val description: String
}

object Commands extends Enum[Command] {

  case object Start extends Command {
    val command: String = "/start"
    val description: String = "Get a welcome message."
  }

  case object Help extends Command {
    val command: String = "/help"
    val description: String = "List all available commands."
  }

  case object Advise extends Command {
    val command: String = "/advise"
    val description: String = "Ask for wine advice, using filters."
  }

  case object Search extends Command {
    val command: String = "/search"
    val description: String = "Search wines by name. Example: /search cabernet"
    val regularExpression: Regex = """/search+\s(.+)""".r
  }

  case object Note extends Command {
    val command: String = "/note"
    val description: String = "Create a note about wine."
  }

  override def values: IndexedSeq[Command] = findValues
}
