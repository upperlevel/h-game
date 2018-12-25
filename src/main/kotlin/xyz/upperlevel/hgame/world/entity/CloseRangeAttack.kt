package xyz.upperlevel.hgame.world.entity

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.Fixture
import com.badlogic.gdx.physics.box2d.FixtureDef
import com.badlogic.gdx.physics.box2d.PolygonShape
import xyz.upperlevel.hgame.world.FixtureSensor

class CloseRangeAttack(val player: Player) {
    private val leftSensor = Sensor()
    private val rightSensor = Sensor()

    val contact: Player?
        get() {
            return if (player.left) leftSensor.player else rightSensor.player
        }

    fun setupFixtures() {
        val leftSensorDef = FixtureDef().apply {
            shape = PolygonShape().apply {
                val w = Player.WIDTH / 4f
                val h = Player.HEIGHT / 2f
                setAsBox(w, h, Vector2(w, h), 0f)
            }
            isSensor = true
        }
        val rightSensorDef = FixtureDef().apply {
            shape = PolygonShape().apply {
                val w = Player.WIDTH / 4f
                val h = Player.HEIGHT / 2f
                setAsBox(w, h, Vector2(w * 3, h), 0f)
            }
            isSensor = true
        }
        player.body.createFixture(leftSensorDef).userData = leftSensor
        player.body.createFixture(rightSensorDef).userData = rightSensor
    }

    private class Sensor : FixtureSensor {
        var player: Player? = null
        var touchCount = 0

        override fun onTouchBegin(other: Fixture, contact: Contact) {
            val entity = other.body.userData
            if (entity == null || entity !is Player) return

            if (player == null) {
                player = entity
            } else if (player != entity) {
                throw RuntimeException("Too many players!")
            }
            touchCount++
        }

        override fun onTouchEnd(other: Fixture, contact: Contact) {
            val entity = other.body.userData
            if (entity == null || entity !is Player) return

            if (entity == player) {
                if (--touchCount == 0) {
                    player = null
                }
            }
        }
    }
}