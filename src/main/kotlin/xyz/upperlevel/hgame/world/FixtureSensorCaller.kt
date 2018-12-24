package xyz.upperlevel.hgame.world

import xyz.upperlevel.hgame.event.EventHandler
import xyz.upperlevel.hgame.event.Listener
import xyz.upperlevel.hgame.world.events.PhysicContactBeginEvent
import xyz.upperlevel.hgame.world.events.PhysicContactEndEvent

class FixtureSensorCaller : Listener {
    @EventHandler
    fun onTouchBegin(e: PhysicContactBeginEvent) {
        val dataA = e.contact.fixtureA.userData
        val dataB = e.contact.fixtureB.userData

        if (dataA is FixtureSensor) {
            dataA.onTouchBegin(e.contact.fixtureB, e.contact)
        }

        if (dataB is FixtureSensor) {
            dataB.onTouchBegin(e.contact.fixtureA, e.contact)
        }
    }

    @EventHandler
    fun onTouchEnd(e: PhysicContactEndEvent) {
        val dataA = e.contact.fixtureA.userData
        val dataB = e.contact.fixtureB.userData

        if (dataA is FixtureSensor) {
            dataA.onTouchEnd(e.contact.fixtureB, e.contact)
        }

        if (dataB is FixtureSensor) {
            dataB.onTouchEnd(e.contact.fixtureA, e.contact)
        }
    }
}