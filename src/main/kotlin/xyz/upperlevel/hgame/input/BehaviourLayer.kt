package xyz.upperlevel.hgame.input

import xyz.upperlevel.hgame.world.character.Entity
import java.util.*

class BehaviourLayer(val entity: Entity) {
    private val behaviours = HashMap<String, Behaviour>()

    var index = 0
    var parent: BehaviourManager? = null

    var active: Behaviour? = null
        set(value) {
            val previous = field
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
            parent?.onBehaviourChange(this, previous, field)
            parent?.endpoint?.send(BehaviourChangePacket(entity.id, index, field?.id))
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
}
