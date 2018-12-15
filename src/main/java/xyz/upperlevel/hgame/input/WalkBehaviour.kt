package xyz.upperlevel.hgame.input

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.math.Vector2
import xyz.upperlevel.hgame.world.character.Player
import xyz.upperlevel.hgame.world.sequence.Sequence

open class WalkBehaviour(behaviourMap: BehaviourMap, id: String, val player: Player, val horizontalImpulse: Float)
             : Behaviour(behaviourMap, id, player) {
    private var animation: Sequence? = null
    var maxVelocity = 7f

    override fun initialize() {
        Behaviour.addDefault(entity, this)
    }

    override fun onEnable() {
        animation = Sequence.create().oscillate({ _, time -> entity.setFrame(time, 1) }, 200, 3).play()
    }

    override fun onPrePhysics() {
        super.onPrePhysics()

        val vel = player.body.linearVelocity

        // cap max velocity on x
        if (Math.abs(vel.x) > maxVelocity) {
            vel.x = Math.signum(vel.x) * maxVelocity
            player.body.linearVelocity = vel
        }

        player.friction = if (player.isTouchingGround) 0.2f else 0f
        val isMaxVelReached = if (horizontalImpulse < 0) vel.x < -maxVelocity else vel.x > maxVelocity
        // apply horizontal impulse, but only if max velocity is not reached yet
        if (!isMaxVelReached) {
            player.body.applyLinearImpulse(Vector2(horizontalImpulse, 0f), player.body.worldCenter, true)
        }
    }

    override fun onDisable() {
        animation?.dismiss()
    }
}

class WalkLeftBehaviour(behaviourMap: BehaviourMap, player: Player)
    : WalkBehaviour(behaviourMap, "walk_left", player,-2f) {
    override fun initialize() {
        super.initialize()
        hook({ !Gdx.input.isKeyPressed(Input.Keys.A) }, behaviourMap["idle"]!!)
    }

    override fun onEnable() {
        super.onEnable()
        entity.left = true
    }
}

class WalkRightBehaviour(behaviourMap: BehaviourMap, player: Player)
    : WalkBehaviour(behaviourMap, "walk_right", player, 2f) {
    override fun initialize() {
        super.initialize()
        hook({ !Gdx.input.isKeyPressed(Input.Keys.D) }, behaviourMap["idle"]!!)
    }

    override fun onEnable() {
        super.onEnable()
        entity.left = false
    }
}
