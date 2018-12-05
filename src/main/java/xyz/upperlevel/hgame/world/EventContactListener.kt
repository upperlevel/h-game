package xyz.upperlevel.hgame.world

import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.ContactImpulse
import com.badlogic.gdx.physics.box2d.ContactListener
import com.badlogic.gdx.physics.box2d.Manifold
import xyz.upperlevel.hgame.HGame
import xyz.upperlevel.hgame.event.EventChannel
import xyz.upperlevel.hgame.screens.GameScreen
import xyz.upperlevel.hgame.world.events.PhysicContactBeginEvent
import xyz.upperlevel.hgame.world.events.PhysicContactEndEvent

class EventContactListener(val channel: EventChannel) : ContactListener {
    override fun beginContact(contact: Contact?) {
        channel.call(PhysicContactBeginEvent(contact!!))
    }

    override fun endContact(contact: Contact?) {
        channel.call(PhysicContactEndEvent(contact!!))
    }

    override fun preSolve(contact: Contact?, oldManifold: Manifold?) {
    }

    override fun postSolve(contact: Contact?, impulse: ContactImpulse?) {
    }
}