package xyz.upperlevel.hgame.behaviour

abstract class Behaviour(val map: BehaviourMap) {
    private val _exitTriggers: MutableMap<Trigger, Behaviour> = HashMap()

    val exitTriggers: Map<Trigger, Behaviour>
        get() = _exitTriggers

    fun addExitTrigger(trigger: Trigger, exit: Behaviour) {
        trigger.onChange = { changeTrigger(exit) }
        _exitTriggers.put(trigger, exit)
    }

    private fun changeTrigger(next: Behaviour) {
        map.current = next
    }

    abstract fun onEnter()

    abstract fun onLeave()
}