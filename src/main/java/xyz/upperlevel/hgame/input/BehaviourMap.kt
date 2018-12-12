package xyz.upperlevel.hgame.input

import xyz.upperlevel.hgame.network.Endpoint
import xyz.upperlevel.hgame.world.character.Entity
import java.util.*

class BehaviourMap(val entity: Entity) {
    private val behaviours = HashMap<String, Behaviour>()
    var endpoint: Endpoint? = null

    var active: Behaviour? = null
        set(value) {
            field?.onDisable()
            var newVal = value
            do {
                field = newVal
                newVal = field?.onEnable()
            } while (newVal != null)
            endpoint?.send(BehaviourChangePacket(entity.id, field?.id))
        }
    var initialized = false
        private set

    fun active(id: String?) {
        active = if (id != null) behaviours[id] else null
    }

    fun register(id: String, behaviour: Behaviour) {
        if (initialized) {
            throw RuntimeException("Cannot register behaviour once initialized")
        }
        if (id in behaviours) {
            throw RuntimeException("Behaviour conflict, two ids registered with the same behaviour")
        }
        behaviours[id] = behaviour
        behaviour.initialize()
    }

    fun register(behaviour: Behaviour) {
        register(behaviour.id, behaviour)
    }

    operator fun get(id: String): Behaviour? {
        return behaviours[id]
    }

    operator fun set(id: String, behaviour: Behaviour) {
        register(id, behaviour)
    }

    operator fun contains(id: String): Boolean {
        return id in behaviours
    }

    fun initialize(startBehaviour: String) {
        if (startBehaviour !in behaviours) throw IllegalArgumentException("startBehaviour not found")
        behaviours.values.forEach { it.initialize() }
        active = behaviours[startBehaviour]
    }

    companion object {
        fun createDefault(entity: Entity): BehaviourMap {
            return BehaviourMap(entity).apply {
                register(IdleBehaviour(this, entity))
                register(WalkLeftBehaviour(this, entity))
                register(WalkRightBehaviour(this, entity))
                register(JumpBehaviour(this, entity))
                initialize("idle")
            }
        }
    }
}
