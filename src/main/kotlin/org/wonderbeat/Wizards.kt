package org.wonderbeat

import model.*
import model.Unit
import org.funktionale.option.firstOption
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("org.wonderbeat.aicup.tick")

data class Position(val x: Int, val y: Int)

data class GameTick(val wizard: Wizard, val world: World, val game: Game, val move: Move)

fun GameTick.turnTo(position: Position) {
    this.move.turn = this.wizard.turnAngleTo(position)
}

fun GameTick.turnTo(unit: Unit) {
    this.move.turn = this.wizard.turnAngleTo(unit)
}

fun GameTick.turnFrom(unit: Unit) {
    this.move.turn = this.wizard.turnAngleFrom(unit)
}

fun GameTick.turnFromCollisionsIfAny() {

    this.wizard.hasCollizionsWith(this.world).firstOption().forEach {
        logger.debug("Collision with $it")
        this.move.turn = this.wizard.turnAngleFrom(it)
    }
}

fun Wizard.turnAngleTo(unit: Unit) = this.getAngleTo(unit) - this.angle
fun Wizard.turnAngleTo(position: Position) = this.getAngleTo(position.x.toDouble(), position.y.toDouble()) - this.angle
fun Wizard.turnAngleFrom(unit: Unit) = -this.turnAngleTo(unit)

fun Wizard.hasCollizionsWith(world: World): List<LivingUnit> {
    val collisionObjects: List<LivingUnit> = arrayListOf<LivingUnit>() + world.buildings +
            world.trees + world.minions + world.wizards.filter { !it.isMe }
    return collisionObjects.filter { this.getDistanceTo(it) <= this.radius + it.radius + 5 }
}

