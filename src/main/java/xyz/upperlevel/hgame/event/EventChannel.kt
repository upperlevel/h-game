package xyz.upperlevel.hgame.event

import xyz.upperlevel.hgame.event.EventListener.Companion.listener
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.superclasses
import kotlin.reflect.full.valueParameters
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.jvmErasure

class EventChannel {
    private val byListenerAndPriority = HashMap<Class<*>, MutableMap<Byte, MutableSet<EventListener<*>>>>()
    private val byEventBaked = HashMap<Class<*>, Array<EventListener<*>>>()

    private var children: MutableSet<EventChannel> = HashSet()

    var parent: EventChannel? = null
        set(value) {
            field?.children?.remove(this)
            field = value
            value?.children?.add(this)
            rebakeAll()
        }

    var exceptionHandler: (Throwable) -> Unit = { e ->
        if (e is Exception)
            e.printStackTrace()
        else
            throw IllegalStateException(e)
    }

    /**
     * Creates a listener with the given arguments and registers it<br></br>
     * Listeners registered with this method cannot be unregistered
     * @param clazz the class of event to listen
     * @param consumer the listener
     * @param priority the priority of the listener
     * @param <E> the event to listen
    </E> */
    fun <E : Event> register(clazz: Class<E>, consumer: (E) -> Unit, priority: Byte = EventPriority.NORMAL) {
        register(listener(clazz, consumer, priority))
    }

    fun call(event: CancellableEvent): Boolean {
        call(event as Event)
        return !event.isCancelled
    }

    fun register(events: Iterator<EventListener<*>>) {
        while (events.hasNext())
            register(events.next())
    }

    fun register(listener: EventListener<*>) {
        val handlers = byListenerAndPriority.computeIfAbsent(listener.clazz) { HashMap() }

        val l = handlers.computeIfAbsent(listener.priority) { k -> HashSet() }
        l.add(listener)
        bake(listener.clazz)
    }

    fun unregister(events: Iterator<EventListener<*>>) {
        while (events.hasNext())
            unregister(events.next())
    }

    fun unregister(listener: EventListener<*>): Boolean {
        val handlers = byListenerAndPriority[listener.clazz] ?: return false

        val priorityMapped = handlers[listener.priority] ?: return false
        return if (priorityMapped.remove(listener)) {
            bake(listener.clazz)
            true
        } else
            false
    }

    inline fun <reified T : Listener> register(listener: T) {
        register(listener, T::class)
    }

    fun register(listener: Listener, clazz: KClass<*>) {
        val methods = clazz.declaredFunctions
        for (method in methods) {
            val handler = method.findAnnotation<EventHandler>() ?: continue

            val l: EventListener<*>
            try {
                l = eventHandlerToListener(listener, method, handler.priority)
            } catch (e: Exception) {
                throw RuntimeException("Exception caught while registering " + listener.javaClass.simpleName + ":" + method.name, e)
            }

            register(l)
        }
        clazz.superclasses.forEach {
            register(listener, it)
        }
    }


    inline fun <reified T : Listener> unregister(listener: T) {
        unregister(listener, T::class)
    }

    fun unregister(listener: Listener, clazz: KClass<*>) {
        val methods = clazz.declaredFunctions
        for (method in methods) {
            val handler = method.findAnnotation<EventHandler>() ?: continue
            val l: EventListener<*>
            try {
                l = eventHandlerToListener(listener, method, handler.priority)
            } catch (e: Exception) {
                throw RuntimeException("Exception caught while registering " + listener.javaClass.simpleName + ":" + method.name, e)
            }

            if (!unregister(l))
                throw IllegalStateException("Cannot remove method $method")
        }
        clazz.superclasses.forEach {
            unregister(listener, it)
        }
    }


    private fun eventHandlerToListener(instance: Any, listener: KFunction<*>, priority: Byte): EventListener<*> {
        if (listener.valueParameters.size != 1) {
            throw IllegalArgumentException("Cannot derive EventListener from the argument method: bad argument number (expected: 1, found: ${listener.parameters.size})")
        }

        listener.isAccessible = true

        val argument = listener.valueParameters[0].type.jvmErasure

        return ReflectionEventListener(argument.java, priority, listener, instance)
    }

    private fun call0(event: Event, clazz: Class<*>?) {
        if (clazz == null) return
        val listeners = byEventBaked[clazz]

        if (listeners != null) {
            // If this class is in the map
            // Call the event
            for (listener in listeners) {
                listener as EventListener<Event>
                listener.call(event)
            }
            //And exit
            return
        }
        // If it wasn't in the map
        // And it can have event children
        if (isBase(clazz) && clazz != Event::class.java) {
            call0(event, Event::class.java)
        } else {
            call0(event, clazz.superclass)
        }
    }

    fun call(event: Event) {
        call0(event, event.javaClass)
    }

    private fun isBase(clazz: Class<*>?): Boolean {
        if (clazz == null || clazz.superclass == null) return true
        if (clazz.superclass == Any::class.java) return true
        for (i in clazz.interfaces) {
            if (isBase(i)) {
                return true
            }
        }
        return false
    }

    fun rebakeAll() {
        byListenerAndPriority.keys.forEach { bakeAndAssign(it) }
    }

    fun bake(clazz: Class<*>) {
        for (other in byListenerAndPriority.keys) {
            if (clazz.isAssignableFrom(other)) {
                bakeAndAssign(other)
            }
        }
    }

    private fun bakeAndAssign(clazz: Class<*>) {
        val baked = bake0(clazz)
        if (!baked.isEmpty()) {
            byEventBaked[clazz] = baked.sortedBy { it.first }
                    .map { it.second }
                    .toTypedArray()
        } else {
            byEventBaked.remove(clazz)
        }

        children.forEach {
            it.bakeAndAssign(clazz)
        }
    }

    private fun bake0(clazz: Class<*>): List<Pair<Byte, EventListener<*>>> {
        val handlers = byListenerAndPriority[clazz]
        val baked = ArrayList<Pair<Byte, EventListener<*>>>()

        if (handlers != null) {
            baked.addAll(handlers.entries.flatMap { entry ->
                entry.value.map { Pair(entry.key, it) }
            })
        }

        if (!isBase(clazz)) {
            val superClazz = clazz.superclass
            if (superClazz != CancellableEvent::class.java) {
                baked.addAll(bake0(superClazz))
            }
        }
        if (parent != null) {
            baked.addAll(parent!!.bake0(clazz))
        }
        return baked
    }

    inner class ReflectionEventListener(clazz: Class<*>,
                                        priority: Byte,
                                        val listener: KFunction<*>,
                                        val instance: Any) : EventListener<Event>(clazz, priority) {

        override fun call(event: Event) {
            try {
                listener.call(instance, event)
            } catch (t: Throwable) {
                exceptionHandler(t)
            }
        }

        override fun equals(other: Any?): Boolean {
            if (!super.equals(other)) return false
            other as ReflectionEventListener
            if (this.listener != other.listener) return false
            return this.instance == other.instance
        }

        override fun hashCode(): Int {
            val prime = 59
            var result = 1
            result = result * prime + super.hashCode()
            result = result * prime + listener.hashCode()
            result = result * prime + instance.hashCode()
            return result
        }
    }
}
