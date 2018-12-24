package xyz.upperlevel.hgame.world

import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.Fixture

/**
 * Add this as the userData of the fixture to listen for contacts
 */
interface FixtureSensor {
    fun onTouchBegin(other: Fixture, contact: Contact)

    fun onTouchEnd(other: Fixture, contact: Contact)
}