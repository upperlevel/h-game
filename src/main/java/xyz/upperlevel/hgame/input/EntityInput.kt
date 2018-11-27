package xyz.upperlevel.hgame.input

import java.util.*
import java.util.Collections.unmodifiableList

class EntityInput(actions: List<InputAction>) {
    private val actions: List<InputAction>

    init {
        this.actions = ArrayList(actions)
    }

    fun getActions(): List<InputAction> {
        return unmodifiableList(actions)
    }

    fun onNetworkAction(actionType: Int) {
        actions.stream()
                .filter { a -> a.id == actionType }
                .findAny()
                .orElseThrow { IllegalArgumentException("Illegal action: $actionType") }
                .onTrigger()
    }
}
