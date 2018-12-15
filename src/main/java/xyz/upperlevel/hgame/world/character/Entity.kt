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
import xyz.upperlevel.hgame.input.BehaviourMap
import xyz.upperlevel.hgame.world.World
import xyz.upperlevel.hgame.world.WorldRenderer
import xyz.upperlevel.hgame.world.sequence.Sequence

abstract class Entity(val id: Int,
                      val body: Body,
                      val texSize: Vector2f,
                      val character: Character) {

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


    private var backToIdle: Sequence? = null

    private var animation: Sequence? = null

    var behaviourMap: BehaviourMap? = null

    val groundSensor: Fixture = body.createFixture(createSensor())!!

    init {
        body.userData = this
        val texture = Texture(Gdx.files.internal("images/" + character.texturePath))

        sprite = Sprite(texture)
        sprite.setSize(texSize.x, texSize.y)

        regions = character.getSprites(texture)
    }

    /**
     * Allows to play the given animation with the security that the
     * previous animation is stopped. An [Entity] is supposed
     * to have one animation running per time.
     */
    fun animate(animation: Sequence) {
        logger.debug("Animation asked to be started on {} (id = {}).", character.name, id)

        // If back to idle task existed, better dismiss to avoid issues
        backToIdle?.dismiss()
        backToIdle = null

        val old = this.animation
        if (old != null) {
            logger.debug("Old animation was present, dismissing it.")
            old.dismiss()
        }
        this.animation = animation
        animation.play()
        logger.debug("Animation started successfully.")
    }

    fun setFrame(x: Int, y: Int) {
        sprite.setRegion(regions[x][y])
    }

    open fun update(world: World) {
        // Updates the BehaviourMap, needed to check hooks.
        behaviourMap?.update()
    }

    open fun prePhysicStep(world: World) {
        behaviourMap?.active?.onPrePhysics()
    }

    fun render(renderer: WorldRenderer) {
        if (left != sprite.isFlipX) {
            sprite.flip(true, false)
        }
        sprite.setPosition(x, y)
        sprite.draw(renderer.spriteBatch)
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
