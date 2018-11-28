package xyz.upperlevel.hgame.world.character

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import org.apache.logging.log4j.LogManager
import xyz.upperlevel.hgame.world.Conversation
import xyz.upperlevel.hgame.world.World
import xyz.upperlevel.hgame.world.WorldRenderer
import xyz.upperlevel.hgame.world.character.controllers.Controller
import xyz.upperlevel.hgame.world.scheduler.Scheduler
import xyz.upperlevel.hgame.world.sequence.Sequence
import xyz.upperlevel.hgame.world.sequence.Trigger
import xyz.upperlevel.hgame.world.sequence.Triggers

open class Actor(val id: Int,
                 val character: Character) {

    var x = 0.0f
    var y = 0.0f

    var left = false

    var velocity = Vector2()

    var isTouchingGround = false
        private set

    private val sprite: Sprite
    private val regions: Array<Array<TextureRegion>>

    var rigidBody = true
    var noClip = false

    private var walkToTask = -1
    private var walkTask = -1

    private var sayTask = -1

    private var idle: Boolean = false
    private var walking: Boolean = false
    private var backToIdle: Sequence? = null

    private var animation: Sequence? = null

    val controller = Controller.bind(this)

    init {
        val texture = Texture(Gdx.files.internal("images/" + character.texturePath))

        sprite = Sprite(texture)
        sprite.setSize(WIDTH, HEIGHT)

        regions = SpriteExtractor.grid(texture, 9, 4)
        idle()
    }

    /**
     * Allows to play the given animation with the security that the
     * previous animation is stopped. An [Actor] is supposed
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

    fun setPosition(x: Float, y: Float) {
        this.x = x
        this.y = y
    }

    @Deprecated("")
    fun walkTo(x: Float, speed: Float, reach: Runnable?): Trigger {
        if (this.x == x) {
            reach!!.run()
            return Triggers.NONE
        }
        val absSpeed = Math.abs(speed)
        val left = this.x < x
        val endWhen: Trigger = { left && this.x >= x || !left && this.x <= x }
        walkToTask = Scheduler.start({
            move(if (left) absSpeed else -absSpeed)
            if (endWhen()) {
                reach?.run()
                Scheduler.cancel(walkToTask)
            }
        }, 1, true)
        return endWhen
    }

    fun walkTo(x: Float, speed: Float): Trigger {
        return walkTo(x, speed, null)
    }

    @Deprecated("")
    fun walkTo(who: Actor, distance: Float, speed: Float, reach: Runnable?): Trigger {
        return if (who.x < x) {
            walkTo(who.x + WIDTH / 2.0f + distance, speed, reach)
        } else {
            walkTo(who.x - WIDTH / 2.0f - distance, speed, reach)
        }
    }

    @Deprecated("")
    fun walkTo(who: Actor, speed: Float, reach: Runnable?): Trigger {
        return walkTo(who, 0.5f, speed, reach)
    }

    fun walkTo(who: Actor, speed: Float): Trigger {
        return walkTo(who, speed, null)
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

    fun setVelocity(velocityX: Float, velocityY: Float) {
        this.velocity.set(velocityX, velocityY)
    }

    fun intersect(other: Actor): Boolean {
        return x >= other.x && x <= other.x + WIDTH || x + WIDTH >= other.x && x + WIDTH <= other.x + WIDTH
    }

    fun idle() {
        animate(Sequence.create().repeat({ _, time ->  setFrame(time % 2, 0) }, 200, -1))
    }

    fun control(inputX: Float, inputY: Float) {
        // X-axis
        if (inputX == 0f && inputY == 0f && !idle) {
            idle()
            idle = true
        }

        if (inputX != 0f) {
            move(inputX)
        }
    }

    open fun move(offsetX: Float) {
        left = offsetX < 0
        if (!walking) {
            animate(Sequence.create().repeat({ _, time ->
                val forward = Math.floor(time / 3.0).toInt() % 2 == 0
                setFrame(if (forward) time % 3 else 3 - time % 3 - 1, 1)
            }, 100, -1))
            walking = true
        }
        /*
        if (backToIdle == null) {
           // backToIdle = null; already done by idle() who calls animate().
            backToIdle = Sequence.create().delay(1000).act(this::idle).play();
        }
        */
        x += offsetX
    }

    open fun jump(velocity: Float) {
        setVelocity(0f, velocity)
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

        if (rigidBody) {
            this.velocity.y -= world.gravity * delta
        }
        x += this.velocity.x * delta
        y += this.velocity.y * delta

        if (!noClip && y < world.groundHeight) {
            y = world.groundHeight
            this.velocity.y = 0f
            isTouchingGround = true
        } else {
            isTouchingGround = false
        }
    }

    fun render(renderer: WorldRenderer) {
        if (left != sprite.isFlipX) {
            sprite.flip(true, false)
        }
        sprite.setPosition(x, y)
        sprite.draw(renderer.spriteBatch)
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
        const val WIDTH = 2.0f
        const val HEIGHT = 2.0f
    }
}
