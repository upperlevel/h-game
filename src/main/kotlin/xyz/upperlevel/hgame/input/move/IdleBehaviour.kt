package xyz.upperlevel.hgame.input.move

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import xyz.upperlevel.hgame.input.Behaviour
import xyz.upperlevel.hgame.input.BehaviourLayer
import xyz.upperlevel.hgame.world.World.Companion.TIME_STEP
import xyz.upperlevel.hgame.world.character.Player
import xyz.upperlevel.hgame.world.sequence.Sequence

class IdleBehaviour(behaviourGraph: BehaviourLayer, val player: Player) : Behaviour(behaviourGraph, "idle", player) {
    private var animation: Sequence? = null
    private var stillTime = 0f

    override val animated = true

    override fun initialize() {
        hook({ Gdx.input.isKeyPressed(Input.Keys.A) }, layer["walk_left"]!!)
        hook({ Gdx.input.isKeyPressed(Input.Keys.D) }, layer["walk_right"]!!)
    }

    override fun onEnable() {
        stillTime = 0f // Reset still time
    }

    override fun onPrePhysics() {
        val vel = player.body.linearVelocity
        vel.x *= 0.9f
        player.body.linearVelocity = vel

        stillTime += TIME_STEP
        // TODO: setter caching? (only call when required)
        player.friction  = when {
            !player.isTouchingGround -> 0f // Player flying, disable friction
            stillTime < 0.2f -> 0.2f // Player still sliding (let him slide for 0.2 seconds)
            else -> 100f // Player stopped sliding,
        }
        // TODO: wtf should this code mean?
        /*if(groundedPlatform != null && groundedPlatform.dist == 0) {
            player.applyLinearImpulse(0, -24, pos.x, pos.y);
        }*/
    }

    override fun onDisable() {
    }

    override fun onAnimationEnable() {
        animation = Sequence.create().repeat({ _, time ->  entity.setFrame(time % 2, 0) }, 200, -1).play()
    }

    override fun onAnimationDisable() {
        animation?.dismiss()
    }
}
