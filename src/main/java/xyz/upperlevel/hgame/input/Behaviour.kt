package xyz.upperlevel.hgame.input

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import org.apache.logging.log4j.LogManager
import xyz.upperlevel.hgame.world.character.Entity
import xyz.upperlevel.hgame.world.sequence.Trigger
import java.util.*

open class Behaviour(val behaviourMap: BehaviourMap, val id: String, val entity: Entity) {
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

        private fun tryAdd(behaviour: Behaviour, trigger: Trigger, id: String) {
            val map = behaviour.behaviourMap
            if (id in behaviour.behaviourMap) {
                behaviour.hooks[trigger] = map[id]!!
            } else {
                logger.warn("Cannot find jump behaviour")
            }
        }

        fun addDefault(entity: Entity, behaviour: Behaviour) {
            tryAdd(behaviour, { Gdx.input.isKeyPressed(Input.Keys.W) && entity.isTouchingGround }, "jump")
            tryAdd(behaviour, { Gdx.input.isKeyJustPressed(Input.Keys.SPACE) }, "attack")
            tryAdd(behaviour, { Gdx.input.isKeyJustPressed(Input.Keys.J) }, "special_attack")
        }
    }
}
