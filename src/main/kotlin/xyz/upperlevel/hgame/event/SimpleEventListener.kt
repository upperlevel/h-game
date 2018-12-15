package xyz.upperlevel.hgame.event

class SimpleEventListener<E : Event>(clazz: Class<E>,
                                     private val consumer: (E) -> Unit,
                                     priority: Byte = EventPriority.NORMAL) : EventListener<E>(clazz, priority) {
    override fun call(event: E) {
        consumer(event)
    }

    override fun equals(other: Any?): Boolean {
        return super.equals(other) and
                (this.consumer == (other as SimpleEventListener<*>).consumer)
    }

    override fun hashCode(): Int {
        return super.hashCode() + 59 * consumer.hashCode()
    }
}
