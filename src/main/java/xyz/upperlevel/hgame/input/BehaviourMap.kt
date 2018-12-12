package xyz.upperlevel.hgame.input

import java.lang.IllegalArgumentException
import java.lang.RuntimeException
import java.util.HashMap

class BehaviourMap {
    private val behaviours = HashMap<String, Behaviour>()

    fun register(id: String, behaviour: Behaviour) {
        behaviours[id] = behaviour
    }

    operator fun get(id: String): Behaviour? {
        return behaviours[id]
    }

    operator fun set(id: String, behaviour: Behaviour) {
        if (id in behaviours) {
            throw RuntimeException("Behaviour conflict, two ids registered with the same behaviour")
        }
        behaviours[id] = behaviour
    }

    operator fun contains(id: String): Boolean {
        return id in behaviours
    }
}
