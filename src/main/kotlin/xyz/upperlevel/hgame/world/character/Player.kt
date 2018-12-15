package xyz.upperlevel.hgame.world.character

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.FixtureDef
import com.badlogic.gdx.physics.box2d.PolygonShape
import org.apache.logging.log4j.LogManager
import com.badlogic.gdx.physics.box2d.World as PWorld
import org.lwjgl.util.vector.Vector2f
import xyz.upperlevel.hgame.input.BehaviourMap
import xyz.upperlevel.hgame.world.Conversation
import xyz.upperlevel.hgame.world.World
import xyz.upperlevel.hgame.world.scheduler.Scheduler
import xyz.upperlevel.hgame.world.sequence.Sequence

open class Player(id: Int, world: PWorld, character: Character)
        : Entity(
                id,
                createBody(world),
                Vector2f(WIDTH, HEIGHT),
                character
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
        // Creates a default BehaviourMap for the Player.
        // Each Player should have one.
        behaviourMap = BehaviourMap.createPlayerBehaviour(this)
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
            behaviourMap?.endpoint?.send(PlayerJumpPacket(id))
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
            }
        }

        private fun createBody(world: PWorld): Body {
            val body = world.createBody(bodyDef)
            body.createFixture(bodyFixtureDef)
            return body
        }
    }
}