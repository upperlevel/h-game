package xyz.upperlevel.hgame.input

import xyz.upperlevel.hgame.network.Endpoint
import xyz.upperlevel.hgame.world.character.Entity
import java.lang.IllegalStateException
import java.util.*

class BehaviourMap(val entity: Entity) {
    private val behaviours = HashMap<String, Behaviour>()
    var endpoint: Endpoint? = null

    var active: Behaviour? = null
        set(value) {
            // Disables the old State.
            field?.onDisable()

            // Checks hooks to see if someone is verified.
            var newVal = value
            do {
                field = newVal
                field?.let {
                    newVal = if (it.instantHookCheck) it.resolveHooks()
                    else null
                }
            } while (newVal != null)

            // Now we have the new State, so we can enable and send the change.
            field?.onEnable()
            endpoint?.send(BehaviourChangePacket(entity.id, field?.id))
        }

    var initialized = false
        private set

    fun active(behaviourId: String?) {
        if (behaviourId !in behaviours) {
            throw IllegalArgumentException("behaviourId not found: $behaviourId")
        }
        active = behaviours[behaviourId]
    }

    fun register(id: String, behaviour: Behaviour) {
        if (initialized) {
            throw RuntimeException("Cannot register behaviour once initialized")
        }
        if (id in behaviours) {
            throw RuntimeException("Behaviour conflict, two ids registered with the same behaviour")
        }
        behaviours[id] = behaviour
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
        behaviours.values.forEach { it.initialize() }
        active(startBehaviour)
        initialized = true
    }

    fun update() {
        active?.onUpdate()
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
