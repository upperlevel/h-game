package xyz.upperlevel.hgame.world

import xyz.upperlevel.hgame.event.EventHandler
import xyz.upperlevel.hgame.event.Listener
import xyz.upperlevel.hgame.world.entity.Entity
import xyz.upperlevel.hgame.world.events.PhysicContactBeginEvent
import xyz.upperlevel.hgame.world.events.PhysicContactEndEvent

class EntityGroundListener : Listener {
    @EventHandler
    fun onTouchBegin(e: PhysicContactBeginEvent) {
        val entityA = e.contact.fixtureA.body.userData
        val entityB = e.contact.fixtureB.body.userData

        if (entityA == World.GROUND_DATA && entityB is Entity) {
            entityB.groundContactCount++
        } else if (entityB == World.GROUND_DATA && entityA is Entity) {
            entityA.groundContactCount++
        }
    }

    @EventHandler
    fun onTouchEnd(e: PhysicContactEndEvent) {
        val entityA = e.contact.fixtureA.body.userData
        val entityB = e.contact.fixtureB.body.userData

        if (entityA == World.GROUND_DATA && entityB is Entity) {
            entityB.groundContactCount--
        } else if (entityB == World.GROUND_DATA && entityA is Entity) {
            entityA.groundContactCount--
        }
    }
}