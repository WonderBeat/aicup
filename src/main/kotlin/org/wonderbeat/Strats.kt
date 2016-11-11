package org.wonderbeat

import model.*

class FakeStrat: Strategy {
    override fun move(wizard: Wizard, world: World, game: Game, move: Move) {
        move.turn = 10.0
        move.speed = 100.0
    }
}
