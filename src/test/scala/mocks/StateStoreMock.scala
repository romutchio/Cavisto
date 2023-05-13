package mocks

import bot.StateStore
import bot.domain.State
import cats.effect.{Ref, Sync}
import cats.implicits._


object StateStoreMock {
  def test[F[_]: Sync, S <: State](state: Map[Long, S], default: S): F[StateStore[F, S]] = for {
    ref <- Ref.of[F, Map[Long, S]](state)
  } yield new StateStore[F, S](ref, default)
}
