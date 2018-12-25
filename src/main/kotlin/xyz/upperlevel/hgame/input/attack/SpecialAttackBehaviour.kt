package xyz.upperlevel.hgame.input.attack

import xyz.upperlevel.hgame.input.Behaviour
import xyz.upperlevel.hgame.input.BehaviourLayer
import xyz.upperlevel.hgame.world.entity.Player
import xyz.upperlevel.hgame.world.sequence.Sequence

class SpecialAttackBehaviour(layer: BehaviourLayer, player: Player) : Behaviour(layer, "special_attack", player) {
    private var animation: Sequence? = null

    override val animated = true

    override fun onAnimationEnable() {
        animation = (entity as Player).specialAttack().act {
            // When the animation's finished goes back to NoAttackBehaviour.
            layer.active = layer["none"]
        }.play()
    }

    override fun onAnimationDisable() {
        animation?.dismiss()
    }
}
