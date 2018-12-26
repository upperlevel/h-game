package xyz.upperlevel.hgame.world.character

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.Fixture
import com.badlogic.gdx.physics.box2d.FixtureDef
import com.badlogic.gdx.physics.box2d.PolygonShape
import org.apache.logging.log4j.LogManager
import org.lwjgl.util.vector.Vector2f
import xyz.upperlevel.hgame.input.BehaviourManager
import xyz.upperlevel.hgame.world.World
import xyz.upperlevel.hgame.world.WorldRenderer
import xyz.upperlevel.hgame.world.entity.EntityImpulsePacket
import xyz.upperlevel.hgame.world.entity.EntitySpawnPacket

open class Entity(val entityType: EntityType, val world: World, val active: Boolean) {
    val body: Body = entityType.createBody(world.physics)
    val groundSensor: Fixture = body.createFixture(createSensor())

    var x: Float
        get() = body.position.x
        set(value) = body.setTransform(value, y, 0f)

    var y: Float
        get() = body.position.y
        set(value) = body.setTransform(x, value, 0f)

    val width: Float
        get() = entityType.width

    val height: Float
        get() = entityType.height

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

    var behaviour: BehaviourManager? = null

    private var destroyed = false

    init {
        body.userData = this

        val texture = Texture(Gdx.files.internal("images/" + entityType.texturePath))

        sprite = Sprite(texture)
        sprite.setSize(width, height)
        regions = entityType.getSprites(texture)
    }

    fun setPosition(x: Float, y: Float) {
        this.x = x
        this.y = y
    }

    fun setFrame(x: Int, y: Int) {
        sprite.setRegion(regions[x][y])
    }

    open fun update(world: World) {
        // Updates the BehaviourLayer, needed to check hooks.
        behaviour?.update()
    }

    open fun prePhysicStep(world: World) {
        behaviour?.onPrePhysics()
    }

    fun render(renderer: WorldRenderer) {
        if (left != sprite.isFlipX) {
            sprite.flip(true, false)
        }
        sprite.setPosition(x, y)
        sprite.draw(renderer.spriteBatch)
    }

    fun impulse(powerX: Float, powerY: Float, pointX: Float, pointY: Float, sendPacket: Boolean = true) {
        body.applyLinearImpulse(powerX, powerY, pointX, pointY, true)
        if (sendPacket) {
            world.endpoint.send(EntityImpulsePacket(id, powerX, powerY, pointX, pointY))
        }
    }

    fun chuck(throwable: ThrowableEntity, power: Float, angle: Float, spawnAt: Vector2f = Vector2f(width / 2f, height / 2f)) {
        throwable.thrower = this

        // The middle of the throwable entity.
        val centerX = x + spawnAt.x
        val centerY = y + spawnAt.y

        // Position is in the bottom left corner.
        throwable.x = centerX - throwable.width / 2f
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
                setAsBox(width / 2, 0.1f, Vector2(width / 2f, 0f), 0f)
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

    companion object {
        private val logger = LogManager.getLogger()
    }
}
