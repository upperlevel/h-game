package xyz.upperlevel.hgame.input

import xyz.upperlevel.hgame.world.character.Player
import xyz.upperlevel.hgame.world.sequence.Sequence

class AttackBehaviour(behaviourMap: BehaviourMap, player: Player) : Behaviour(behaviourMap, "attack", player) {
    private var animation: Sequence? = null

    override fun onEnable() {
        animation = (entity as Player).attack().act {
            entity.behaviourMap.let {
                // When the animation's finished goes back to IdleBehaviour.
                it?.active = it?.get("idle")
            }
        }.play()
    }

    override fun onDisable() {
        animation?.dismiss()
    }
}
