package xyz.upperlevel.hgame.world.character

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.FixtureDef
import com.badlogic.gdx.physics.box2d.PolygonShape
import org.apache.logging.log4j.LogManager
import org.lwjgl.util.vector.Vector2f
import xyz.upperlevel.hgame.world.Conversation
import xyz.upperlevel.hgame.world.World
import xyz.upperlevel.hgame.world.WorldRenderer
import xyz.upperlevel.hgame.world.character.controllers.Controller
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


    var left = false

    var isTouchingGround = false
        private set

    private val sprite: Sprite
    private val regions: Array<Array<TextureRegion>>

    private var sayTask = -1

    private var idle: Boolean = false
    private var walking: Boolean = false
    private var backToIdle: Sequence? = null

    private var animation: Sequence? = null

    val controller = Controller.bind(this)

    val groundSensor = body.createFixture(createSensor())

    init {
        val texture = Texture(Gdx.files.internal("images/" + character.texturePath))

        sprite = Sprite(texture)
        sprite.setSize(texSize.x, texSize.y)

        regions = SpriteExtractor.grid(texture, 9, 4)
        idle()
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

    fun idle() {
        animate(Sequence.create().repeat({ _, time ->  setFrame(time % 2, 0) }, 200, -1))
    }

    open fun jump(velocity: Float) {
        body.applyLinearImpulse(Vector2(0f, -velocity), body.worldCenter, true)
    }

    open fun attack() {
        setFrame(2, 0)
        // TODO delay to remove
    }

    open fun specialAttack() {
        // By default, special attack is implemented as a normal attack.
        // The Character should override the Actor class in order to implement its own special attack.
        attack()
    }

    fun update(world: World) {
        val delta = Gdx.graphics.deltaTime
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
                setAsBox(texSize.x, 0.3f)
            }
        }
    }

    inner class Walking : Runnable {
        private var frame: Int = 0
        private var backward: Boolean = false

        override fun run() {
            setFrame(frame, 1)
            if (backward) {
                frame--
            } else {
                frame++
            }
            if (frame < 0) {
                frame = 0
                backward = false
            } else if (frame == 3) {
                frame = 2
                backward = true
            }
        }
    }

    companion object {
        private val logger = LogManager.getLogger()
    }
}
