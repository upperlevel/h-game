package xyz.upperlevel.hgame.world.character

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.FixtureDef
import com.badlogic.gdx.physics.box2d.PolygonShape
import org.apache.logging.log4j.LogManager
import org.lwjgl.util.vector.Vector2f
import xyz.upperlevel.hgame.input.BehaviourManager
import xyz.upperlevel.hgame.world.Conversation
import xyz.upperlevel.hgame.world.World
import xyz.upperlevel.hgame.world.scheduler.Scheduler
import xyz.upperlevel.hgame.world.sequence.Sequence
import com.badlogic.gdx.physics.box2d.World as Physics

open class Player(id: Int, world: World, entityType: EntityType)
        : Entity(
                id,
                world,
                createBody(world.physics),
                Vector2f(WIDTH, HEIGHT),
                entityType
) {

    var active = false

    private var sayTask = -1
    var jumpForce = 40f

    var friction: Float
        set(value) {
            body.fixtureList[0].friction = value
        }
        get() {
            return body.fixtureList[0].friction
        }

    init {
        // Creates a default Behaviour for the Player.
        // Each Player should have one.
        behaviour = BehaviourManager.createPlayerBehaviour(this)
    }

    override fun update(world: World) {
        super.update(world)
        if (active && Gdx.input.isKeyPressed(Input.Keys.W) && isTouchingGround) {
            this.jump()
        }
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

    open fun jump() {
        logger.info("JUMPING")
        body.applyLinearImpulse(Vector2(0f, jumpForce), body.worldCenter, true)
        if (active) {
            behaviour?.endpoint?.send(PlayerJumpPacket(id))
        }
    }

    open fun attack(): Sequence {
        return Sequence.create().act { setFrame(0, 0) }
    }

    open fun specialAttack(): Sequence {
        // By default, special attack is implemented as a normal attack.
        // The Character should override the Actor class in order to implement its own special attack.
        return attack()
    }

    companion object {
        const val WIDTH = 2.0f
        const val HEIGHT = 2.0f

        private val logger = LogManager.getLogger()

        val bodyDef: BodyDef
        val bodyFixtureDef: FixtureDef

        init {
            bodyDef = BodyDef().apply {
                fixedRotation = true
                type = BodyDef.BodyType.DynamicBody
            }

            bodyFixtureDef = FixtureDef().apply {
                shape =  PolygonShape().apply {
                    val w = WIDTH / 2f
                    val h = HEIGHT / 2f
                    setAsBox(w, h, Vector2(w, h), 0f)
                }
                density = 1f
                filter.apply {
                    categoryBits = 0x2 // Category 0x2 (default is 0x1)
                    maskBits = 0x2.inv()// Collides with everything but 0x2
                }
            }
        }

        private fun createBody(physics: Physics): Body {
            val body = physics.createBody(bodyDef)
            body.createFixture(bodyFixtureDef)
            return body
        }
    }
}