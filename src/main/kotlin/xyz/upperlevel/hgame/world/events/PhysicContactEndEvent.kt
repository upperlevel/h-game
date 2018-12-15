package xyz.upperlevel.hgame.world.events

import com.badlogic.gdx.physics.box2d.Contact
import xyz.upperlevel.hgame.event.Event

class PhysicContactEndEvent(val contact: Contact) : Event