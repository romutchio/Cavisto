package database.models

import bot.domain.AdviseState

import java.util.Date

case class AdviseHistory(user_id: Int, advise_state: AdviseState, created_at: Date)
