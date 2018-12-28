package xyz.upperlevel.hgame.world.player

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.math.Vector2
import org.apache.logging.log4j.LogManager
import xyz.upperlevel.hgame.DefaultFont
import xyz.upperlevel.hgame.input.BehaviourManager
import xyz.upperlevel.hgame.world.Conversation
import xyz.upperlevel.hgame.world.World
import xyz.upperlevel.hgame.world.WorldRenderer
import xyz.upperlevel.hgame.world.entity.Entity
import xyz.upperlevel.hgame.world.entity.EntitySpawnPacket
import xyz.upperlevel.hgame.world.entity.EntityType
import xyz.upperlevel.hgame.world.entity.PlayerSpawnPacket
import xyz.upperlevel.hgame.world.scheduler.Scheduler
import xyz.upperlevel.hgame.world.sequence.Sequence
import com.badlogic.gdx.physics.box2d.World as Physics

open class Player(entityType: EntityType, world: World, active: Boolean) : Entity(entityType, world, active) {
    private var sayTask = -1
    var jumpForce = 40f

    var friction: Float
        get() = body.fixtureList[0].friction
        set(value) { body.fixtureList[0].friction = value }

    open val maxEnergy = 1.0f
    var energy = 0f
    var energyPerSecond = 0.05f


    open val attackPower = 0.1f

    var name = "Ulisse"

    override var damageable = true

    init {
        // Creates a default Behaviour for the Player.
        // Each Player should have one.
        behaviour = BehaviourManager.createPlayerBehaviour(this)
    }

    override fun update() {
        super.update()
        if (active && Gdx.input.isKeyPressed(Input.Keys.W) && isTouchingGround) {
            this.jump()
        }
        energy = Math.min(energy + energyPerSecond * Gdx.graphics.deltaTime, maxEnergy)
    }

    override fun serialize(): EntitySpawnPacket {
        return PlayerSpawnPacket(entityType.id, id, x, y, left, name)
    }

    override fun deserialize(packet: EntitySpawnPacket) {
        if (packet !is PlayerSpawnPacket) throw IllegalArgumentException("Given packet is not a PlayerSpawnPacket")
        super.deserialize(packet)
        name = packet.name
    }

    override fun fillResetPacket(data: MutableMap<String, Any>) {
        super.fillResetPacket(data)
        data["life"] = life
        data["energy"] = energy
    }

    override fun onReset(data: Map<String, Any>) {
        super.onReset(data)
        life = (data["life"] as Number).toFloat()
        energy = (data["energy"] as Number).toFloat()
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

    fun onAttacked(player: Player) {
        life -= player.attackPower
        logger.info("$id Life: $life")
        if (life < 0) {
            // TODO: Dead
        }
    }

    override fun renderHud(renderer: WorldRenderer.UIRenderer) {
        val font = DefaultFont.PLAYER_NAME_FONT
        renderer.drawWorldText(font, name, centerX, y + height, true)
    }

    companion object {
        const val WIDTH = 2.0f
        const val HEIGHT = 2.0f

        private val logger = LogManager.getLogger()
    }
}