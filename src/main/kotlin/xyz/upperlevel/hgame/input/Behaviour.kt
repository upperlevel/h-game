package xyz.upperlevel.hgame.input

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import org.apache.logging.log4j.LogManager
import xyz.upperlevel.hgame.world.character.Entity
import xyz.upperlevel.hgame.world.sequence.Trigger
import java.util.*

open class Behaviour(val layer: BehaviourLayer, val id: String, val entity: Entity, var instantHookCheck: Boolean = true) {
    private val hooks = HashMap<Trigger, Behaviour>()
    open val animated = false

    fun hook(trigger: Trigger, behaviour: Behaviour) {
        hooks[trigger] = behaviour
    }

    fun resolveHooks(): Behaviour? {
        logger.debug("Checking hooks...")
        hooks.forEach {
            if (it.key.invoke()) {
                logger.debug("Hook verified! Setting next Behaviour to: ${it.value.id}")
                return it.value
            }
        }
        return null
    }

    open fun initialize() {
    }

    open fun onEnable() {
    }

    open fun onUpdate() {
        // Each time checks if there is a hook verified.
        val next = resolveHooks()
        if (next != null) {
            layer.active = next
        }
    }

    open fun onPrePhysics() {
    }

    open fun onDisable() {
        logger.debug("Behaviour disabled: $id")
    }

    open fun onAnimationEnable() {
    }

    open fun onAnimationDisable() {
    }

    companion object {
        val logger = LogManager.getLogger()

        fun tryAdd(behaviour: Behaviour, trigger: Trigger, id: String) {
            val map = behaviour.layer
            if (id in behaviour.layer) {
                behaviour.hooks[trigger] = map[id]!!
            } else {
                logger.warn("Cannot find $id behaviour")
            }
        }
    }
}
