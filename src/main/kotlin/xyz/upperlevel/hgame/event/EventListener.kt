package xyz.upperlevel.hgame.event

abstract class EventListener<E : Event> (
        val clazz: Class<*>,
        val priority: Byte = EventPriority.NORMAL) {
    abstract fun call(event: E)

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other == null) return false
        if (other.javaClass != this.javaClass) return false
        other as EventListener<*> // Come on kotlin
        if (this.clazz != other.clazz) return false
        return this.priority == other.priority
    }

    override fun hashCode(): Int {
        val prime = 59
        var result = 1
        result = result * prime + this.clazz.hashCode()
        result = result * prime + this.priority
        return result
    }

    companion object {
        fun <E : Event> listener(clazz: Class<E>, consumer: (E) -> Unit, priority: Byte = EventPriority.NORMAL): EventListener<E> {
            return SimpleEventListener(clazz, consumer, priority)
        }

        fun <E : Event> of(clazz: Class<E>, consumer: (E) -> Unit, priority: Byte = EventPriority.NORMAL): EventListener<E> {
            return SimpleEventListener(clazz, consumer, priority)
        }
    }
}
