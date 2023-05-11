package database.domain

import bot.domain.states.AdviseState

import java.util.Date

case class AdviseHistory(user_id: Int, advise_state: AdviseState, created_at: Date)
