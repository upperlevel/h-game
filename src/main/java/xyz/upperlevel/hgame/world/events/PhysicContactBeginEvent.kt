package xyz.upperlevel.hgame.world.events

import com.badlogic.gdx.physics.box2d.Contact
import xyz.upperlevel.hgame.event.Event

class PhysicContactBeginEvent(val contact: Contact) : Event