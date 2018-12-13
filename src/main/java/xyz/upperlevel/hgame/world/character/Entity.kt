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
import xyz.upperlevel.hgame.world.Conversation
import xyz.upperlevel.hgame.world.World
import xyz.upperlevel.hgame.world.WorldRenderer
import xyz.upperlevel.hgame.world.scheduler.Scheduler
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
    var moveForce: Vector2? = null
        set(value) {
            field = value
            if (field != null) {
                left = field?.x!! < 0
            }
        }

    val isTouchingGround: Boolean
        // has ground contact AND the velocity is going down (or static)
        // if the velocity is going up it means the jump has begun
        get() = groundContactCount > 0 && body.linearVelocity.y <= 0

    var groundContactCount = 0

    private val sprite: Sprite
    private val regions: Array<Array<TextureRegion>>

    private var sayTask = -1

    private var backToIdle: Sequence? = null

    private var animation: Sequence? = null

    var behaviourMap: BehaviourMap? = null

    val groundSensor: Fixture = body.createFixture(createSensor())!!

    init {
        body.userData = this
        val texture = Texture(Gdx.files.internal("images/" + character.texturePath))

        sprite = Sprite(texture)
        sprite.setSize(texSize.x, texSize.y)

        regions = SpriteExtractor.grid(texture, 9, 4)
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

    fun say(text: String, audio: String, duration: Long) {
        if (sayTask != -1) {
            Scheduler.cancel(sayTask)
            sayTask = -1
        }
        if (duration > 0) {
            sayTask = Scheduler.start({
                // GameScreen.instance.getScenario().setRenderingSentence(this, null);
            }, duration)
        }
        Conversation.show(this, text, audio)
    }

    fun say(text: String, audioPath: String) {
        say(text, audioPath, -1)
    }

    open fun jump(velocity: Float) {
        logger.info("JUMPING")
        body.applyLinearImpulse(Vector2(0f, velocity), body.worldCenter, true)
    }

    open fun attack(): Sequence {
        return Sequence.create().act { setFrame(0, 0) }
    }

    open fun specialAttack(): Sequence {
        // By default, special attack is implemented as a normal attack.
        // The Character should override the Actor class in order to implement its own special attack.
        return attack()
    }

    fun update(world: World) {
        // If there's a movement force, apply it
        moveForce?.let { body.applyForce(it, body.worldCenter, true) }

        // Updates the BehaviourMap, needed to check hooks.
        behaviourMap?.update()
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
