package xyz.upperlevel.hgame.input

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import org.apache.logging.log4j.LogManager
import xyz.upperlevel.hgame.world.character.Actor
import xyz.upperlevel.hgame.world.sequence.Trigger
import xyz.upperlevel.hgame.world.sequence.Triggers
import java.util.*

open class Behaviour(val behaviourMap: BehaviourMap, val id: String, val actor: Actor) {
    private val hooks = HashMap<Trigger, Behaviour>()

    fun hook(trigger: Trigger, behaviour: Behaviour) {
        hooks[trigger] = behaviour
    }

    fun firstActiveHook(): Behaviour? {
        hooks.forEach {
            if (it.key.invoke()) {
                return it.value
            }
        }
        return null
    }

    open fun initialize() {
    }

    open fun onEnable(): Behaviour? {
        return firstActiveHook()
    }

    open fun onDisable() {}

    companion object {
        val logger = LogManager.getLogger()
        val JUMP_TRIGGER: Trigger = { Gdx.input.isKeyPressed(Input.Keys.ENTER) /* TODO && player.isTouchingGround */ }
        val ATTACK_TRIGGER: Trigger = { Gdx.input.isKeyJustPressed(Input.Keys.SPACE) }
        val SPECIAL_ATTACK_TRIGGER: Trigger = { Gdx.input.isKeyJustPressed(Input.Keys.J) }

        private fun tryAdd(behaviour: Behaviour, trigger: Trigger, id: String) {
            val map = behaviour.behaviourMap
            if (id in behaviour.behaviourMap) {
                behaviour.hooks[trigger] = map[id]!!
            } else {
                logger.warn("Cannot find jump behaviour")
            }
        }

        fun addDefault(behaviour: Behaviour) {
            val map = behaviour.behaviourMap
            tryAdd(behaviour, JUMP_TRIGGER, "jump")
            tryAdd(behaviour, ATTACK_TRIGGER, "attack")
            tryAdd(behaviour, SPECIAL_ATTACK_TRIGGER, "special_attack")
        }
    }
}
