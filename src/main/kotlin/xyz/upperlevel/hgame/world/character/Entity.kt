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

open class Entity(val id: Int,
                  val world: World,
                  val body: Body,
                  val texSize: Vector2f,
                  val entityType: EntityType) {

    val x: Float
        get() = body.position.x

    val y: Float
        get() = body.position.y

    var left: Boolean = false

    val isTouchingGround: Boolean
        // has ground contact AND the velocity is going down (or static)
        // if the velocity is going up it means the jump has begun
        get() = groundContactCount > 0 && body.linearVelocity.y <= 0

    var groundContactCount = 0

    private val sprite: Sprite
    private val regions: Array<Array<TextureRegion>>

    var behaviour: BehaviourManager? = null

    val groundSensor: Fixture = body.createFixture(createSensor())!!

    var destroyed: Boolean = false

    init {
        body.userData = this
        val texture = Texture(Gdx.files.internal("images/" + entityType.texturePath))

        sprite = Sprite(texture)
        sprite.setSize(texSize.x, texSize.y)

        regions = entityType.getSprites(texture)
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

    fun destroy() {
        if (!destroyed) {
            world.physics.destroyBody(body)
            destroyed = true
        }
    }

    private fun createSensor(): FixtureDef {
        return FixtureDef().apply {
            isSensor = true
            shape = PolygonShape().apply {
                setAsBox(texSize.x / 2, 0.1f, Vector2(texSize.x / 2f, 0f), 0f)
            }
        }
    }

    companion object {
        private val logger = LogManager.getLogger()
    }
}
