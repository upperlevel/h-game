package xyz.upperlevel.hgame.world.entity

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.Fixture
import com.badlogic.gdx.physics.box2d.FixtureDef
import com.badlogic.gdx.physics.box2d.PolygonShape
import org.apache.logging.log4j.LogManager
import xyz.upperlevel.hgame.input.BehaviourManager
import xyz.upperlevel.hgame.world.World
import xyz.upperlevel.hgame.world.WorldRenderer
import xyz.upperlevel.hgame.world.sequence.Sequence

open class Entity(val entityType: EntityType, val world: World, val active: Boolean) {
    val body: Body = entityType.createBody(world.physics)
    val groundSensor: Fixture = body.createFixture(createSensor())

    val width: Float
        get() = entityType.width

    val height: Float
        get() = entityType.height

    var x: Float
        get() = body.position.x
        set(value) = body.setTransform(value, y, 0f)

    var y: Float
        get() = body.position.y
        set(value) = body.setTransform(x, value, 0f)

    val centerX: Float
        get() = x

    val centerY: Float
        get() = y + height / 2f

    var left: Boolean = false

    var id: Int = -1
    val spawned: Boolean
        get() = id >= 0

    val isTouchingGround: Boolean
        // has ground contact AND the velocity is going down (or static)
        // if the velocity is going up it means the jump has begun
        get() = groundContactCount > 0 && body.linearVelocity.y <= 0

    var groundContactCount = 0

    private val sprite: Sprite
    private val regions: Array<Array<TextureRegion>>
        get() = entityType.regions
    var color: Color = Color.WHITE

    var behaviour: BehaviourManager? = null

    open val maxLife = 1.0f
    var life = maxLife
    open var damageable = false

    private var destroyed = false

    init {
        body.userData = this

        sprite = Sprite(entityType.texture)
        sprite.setSize(width, height)
    }

    fun setPosition(x: Float, y: Float) {
        this.x = x
        this.y = y
    }

    fun setFrame(x: Int, y: Int) {
        sprite.setRegion(regions[x][y])
    }

    open fun update() {
        // Updates the BehaviourLayer, needed to check hooks.
        behaviour?.update()
    }

    open fun prePhysicStep(world: World) {
        behaviour?.onPrePhysics()
    }

    fun damage(amount: Float) {
        if (damageable) {
            life -= amount

            val speed = 100
            val steps = 10
            Sequence.create()
                    .act { world.showPopup("%.2f".format(amount), centerX, centerY) }
                    .repeat({ _, time ->
                        color = Color(1.0f, time / (steps - 1.0f), time / (steps - 1.0f), 1.0f)
                    }, speed / steps.toLong(), steps)
                    .play()
        }
    }

    fun impulse(powerX: Float, powerY: Float, pointX: Float, pointY: Float, sendPacket: Boolean = true) {
        body.applyLinearImpulse(powerX, powerY, pointX, pointY, true)
        if (sendPacket) {
            world.endpoint.send(EntityImpulsePacket(id, powerX, powerY, pointX, pointY))
        }
    }

    fun chuck(throwable: ThrowableEntity, power: Float, angle: Float) {
        throwable.thrower = this

        throwable.x = centerX
        throwable.y = centerY - throwable.height / 2f

        world.spawn(throwable)

        // After the entity has been spawned we apply the impulse.
        var powerX = (Math.cos(angle.toDouble()) * power).toFloat()
        val powerY = (Math.sin(angle.toDouble()) * power).toFloat()
        powerX = if (left) -powerX else powerX

        throwable.impulse(powerX, powerY, centerX, centerY)
    }

    open fun serialize(): EntitySpawnPacket {
        return EntitySpawnPacket(entityType.id, id, x, y, left)
    }

    open fun deserialize(packet: EntitySpawnPacket) {
        if (entityType.id != packet.entityTypeId) throw IllegalStateException("Mismatching entity type id and packet's entity type id.")
        id = packet.entityId
        x = packet.x
        y = packet.y
        left = packet.isFacingLeft
    }

    private fun createSensor(): FixtureDef {
        return FixtureDef().apply {
            isSensor = true
            shape = PolygonShape().apply {
                setAsBox(width / 2, 0.1f)
            }
        }
    }

    open fun fillResetPacket(data: MutableMap<String, Any>) {
        data["x"] = x
        data["y"] = y
    }

    open fun onReset(data: Map<String, Any>) {
        val x = data["x"] as Number
        val y = data["y"] as Number
        body.setTransform(x.toFloat(), y.toFloat(), 0f)
    }

    open fun render(renderer: WorldRenderer) {
        sprite.color = color
        if (left != sprite.isFlipX) {
            sprite.flip(true, false)
        }
        sprite.setPosition(x - width / 2f, y)
        sprite.draw(renderer.spriteBatch)
    }

    open fun renderHud(renderer: WorldRenderer.UIRenderer) {
        // No HUD is rendered for default entity.
    }

    companion object {
        private val logger = LogManager.getLogger()
    }
}
