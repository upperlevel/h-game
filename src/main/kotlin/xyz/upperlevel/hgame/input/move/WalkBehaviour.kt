package xyz.upperlevel.hgame.input.move

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.math.Vector2
import xyz.upperlevel.hgame.input.Behaviour
import xyz.upperlevel.hgame.input.BehaviourLayer
import xyz.upperlevel.hgame.world.entity.Player
import xyz.upperlevel.hgame.world.sequence.Sequence

open class WalkBehaviour(behaviourGraph: BehaviourLayer, id: String, val player: Player, val horizontalImpulse: Float)
             : Behaviour(behaviourGraph, id, player) {
    private var animation: Sequence? = null
    var maxVelocity = 7f

    override val animated = true

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

    override fun onAnimationEnable() {
        animation = Sequence.create().oscillate({ _, time -> entity.setFrame(time, 1) }, 200, 3).play()
    }

    override fun onAnimationDisable() {
        animation?.dismiss()
    }
}

class WalkLeftBehaviour(behaviourGraph: BehaviourLayer, player: Player)
    : WalkBehaviour(behaviourGraph, "walk_left", player,-2f) {
    override fun initialize() {
        super.initialize()
        hook({ !Gdx.input.isKeyPressed(Input.Keys.A) }, layer["idle"]!!)
    }

    override fun onEnable() {
        super.onEnable()
        entity.left = true
    }
}

class WalkRightBehaviour(behaviourGraph: BehaviourLayer, player: Player)
    : WalkBehaviour(behaviourGraph, "walk_right", player, 2f) {
    override fun initialize() {
        super.initialize()
        hook({ !Gdx.input.isKeyPressed(Input.Keys.D) }, layer["idle"]!!)
    }

    override fun onEnable() {
        super.onEnable()
        entity.left = false
    }
}
