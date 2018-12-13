package xyz.upperlevel.hgame.input

import xyz.upperlevel.hgame.world.character.Entity
import xyz.upperlevel.hgame.world.sequence.Sequence

class AttackBehaviour(behaviourMap: BehaviourMap, entity: Entity) : Behaviour(behaviourMap, "attack", entity) {
    private var animation: Sequence? = null

    override fun onEnable() {
        animation = entity.attack().act {
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
