package xyz.upperlevel.hgame.input

import xyz.upperlevel.hgame.input.attack.AttackBehaviour
import xyz.upperlevel.hgame.input.attack.NoAttackBehaviour
import xyz.upperlevel.hgame.input.attack.SpecialAttackBehaviour
import xyz.upperlevel.hgame.input.move.IdleBehaviour
import xyz.upperlevel.hgame.input.move.WalkLeftBehaviour
import xyz.upperlevel.hgame.input.move.WalkRightBehaviour
import xyz.upperlevel.hgame.network.Endpoint
import xyz.upperlevel.hgame.world.character.Player

/**
 * This class manages various BehaviourGraphs organized as layers
 *
 */
class BehaviourManager(val layers: List<BehaviourLayer>) {
    var currentAnimated: Behaviour? = null
        set(value) {
            field?.onAnimationDisable()
            field = value
            field?.onAnimationEnable()
        }

    var endpoint: Endpoint? = null

    init {
        layers.forEachIndexed { index, layer ->
            layer.parent = this
            layer.index = index
        }
        currentAnimated = searchAnimation()
    }

    fun update() {
        layers.forEach { it.update() }
    }

    fun onPrePhysics() {
        layers.forEach { it.active?.onPrePhysics() }
    }

    fun searchAnimation(startLayer: Int = -1): Behaviour? {
        var currLayer = if (startLayer == -1) layers.size - 1 else startLayer
        do {
            layers[currLayer].active?.let {
                if (it.animated) return it
            }
        } while (--currLayer >= 0)
        return null
    }

    fun onBehaviourChange(layer: BehaviourLayer, previous: Behaviour?, next: Behaviour?) {
        val current = currentAnimated

        if (current == null) {
            // The current one is null
            // use the new one only if animated, else keep the null animation
            if (next != null && next.animated) currentAnimated = next
        } else if (current == previous) {
            // Substitution in the same layer
            // if the next one is animated then choose it
            // else search an animation in the lower layers
            currentAnimated = if (next != null && next.animated) next else searchAnimation(current.layer.index - 1)
        } else if (next != null && next.animated && current.layer.index <= next.layer.index) {
            // the next is at an higher layer than the current one (and it is animated)
            currentAnimated = next
        }
    }

    override fun toString(): String {
        return "[" +
                layers.joinToString(" + ") { (if (it.active == currentAnimated) ">" else "") + it.active?.id } +
                "]"
    }

    companion object {
        fun createPlayerBehaviour(entity: Player): BehaviourManager {
            val moveLayer = BehaviourLayer(entity).apply {
                register(IdleBehaviour(this, entity))
                register(WalkLeftBehaviour(this, entity))
                register(WalkRightBehaviour(this, entity))
                initialize("idle")
            }
            val attackLayer = BehaviourLayer(entity).apply {
                register(NoAttackBehaviour(this, entity))
                register(AttackBehaviour(this, entity))
                register(SpecialAttackBehaviour(this, entity))
                initialize("none")
            }
            // move is the lower layer so that attack animations take priority
            return BehaviourManager(listOf(moveLayer, attackLayer))
        }
    }
}