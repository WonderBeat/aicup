package org.wonderbeat

import model.BuildingType
import model.LivingUnit
import org.funktionale.option.toOption


class FakeStrat() {
    fun move(tick: GameTick) {
        val distanceTower = tick.world.buildings
                .filter { it.type == BuildingType.GUARDIAN_TOWER }
                .sortedBy { tick.wizard.getDistanceTo(it) }.last()
        val collisionObjects: List<LivingUnit> =
            arrayListOf<LivingUnit>() + tick.world.buildings +
                    tick.world.trees +
                    tick.world.minions +
                    tick.world.wizards.filter { !it.isMe }
        val finder = PathNavigator(tick.game.mapSize.toInt(),
                {
                    collisionObjects.any {
                        obj ->
                        obj.getDistanceTo(it.x.toDouble(), it.y.toDouble()) < obj.radius + tick.wizard.radius
                    }
                }, 200)
        val nextWaypoint = finder.path(Position(tick.wizard.x.toInt(), tick.wizard.y.toInt()),
                Position(distanceTower.x.toInt(), distanceTower.y.toInt()))?.first()
        nextWaypoint.toOption().forEach { tick.turnTo(it) }
        tick.turnFromCollisionsIfAny()
        tick.move.speed = 100.0
    }
}
