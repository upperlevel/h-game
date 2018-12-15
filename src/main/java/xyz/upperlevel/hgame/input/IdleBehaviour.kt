package xyz.upperlevel.hgame.input

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import xyz.upperlevel.hgame.world.World.Companion.TIME_STEP
import xyz.upperlevel.hgame.world.character.Player
import xyz.upperlevel.hgame.world.sequence.Sequence

class IdleBehaviour(behaviourMap: BehaviourMap, val player: Player) : Behaviour(behaviourMap, "idle", player) {
    private var animation: Sequence? = null
    private var stillTime = 0f

    override fun initialize() {
        hook({ Gdx.input.isKeyPressed(Input.Keys.A) }, behaviourMap["walk_left"]!!)
        hook({ Gdx.input.isKeyPressed(Input.Keys.D) }, behaviourMap["walk_right"]!!)
        Behaviour.addDefault(entity, this)
    }

    override fun onEnable() {
        animation = Sequence.create().repeat({ _, time ->  entity.setFrame(time % 2, 0) }, 200, -1).play()
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
        animation?.dismiss()
    }
}
