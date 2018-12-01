package xyz.upperlevel.hgame.behaviour

enum class TriggerType {
    ACTION, STATE
}

open class Trigger(val type: TriggerType) {
    var active = false
        protected set(value) {
            onChange(value)
            field = value
        }

    var onChange: (Boolean) -> Unit = {}

    companion object {
        fun and(type: TriggerType, vararg triggers: Trigger): UnionTrigger {
            return UnionTrigger(type).apply {
                triggers.forEach { addTrigger(it) }
            }
        }
    }
}

class UnionTrigger(type: TriggerType) : Trigger(type) {
    private var _triggers: MutableSet<Trigger> = HashSet()

    val triggers: Set<Trigger>
            get() = _triggers

    fun addTrigger(trigger: Trigger) {
        trigger.onChange = { refresh() }
        _triggers.add(trigger)
    }

    fun removeTrigger(trigger: Trigger) {
        _triggers.remove(trigger)
    }

    private fun refresh() {
        val newState = _triggers.all { it.active }
        if (active == newState) return
        active = newState
    }
}


