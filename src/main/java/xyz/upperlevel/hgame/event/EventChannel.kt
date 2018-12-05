package xyz.upperlevel.hgame.event

import xyz.upperlevel.hgame.event.EventListener.Companion.listener
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import java.lang.reflect.Method
import java.util.*
import java.util.function.Consumer
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class EventChannel {
    private val byListenerAndPriority = HashMap<Class<*>, MutableMap<Byte, MutableSet<EventListener<*>>>>()
    private val byEventBaked = HashMap<Class<*>, Array<EventListener<*>>>()

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

    fun register(listener: Listener) {
        register0(listener, listener.javaClass)
    }

    private fun register0(listener: Listener, clazz: Class<*>) {
        val methods = clazz.declaredMethods
        for (method in methods) {
            val handler = method.getAnnotation(EventHandler::class.java) ?: continue

            val l: EventListener<*>
            try {
                l = eventHandlerToListener(listener, method, handler.priority)
            } catch (e: Exception) {
                throw RuntimeException("Exception caught while registering " + listener.javaClass.simpleName + ":" + method.name, e)
            }

            register(l)
        }
        if (clazz.superclass != null)
            register0(listener, clazz.superclass)
    }


    fun unregister(listener: Listener) {
        unregister0(listener, listener.javaClass)
    }

    private fun unregister0(listener: Listener, clazz: Class<*>) {
        val methods = clazz.methods
        for (method in methods) {
            val handler = method.getAnnotation(EventHandler::class.java) ?: continue
            val l: EventListener<*>
            try {
                l = eventHandlerToListener(listener, method, handler.priority)
            } catch (e: Exception) {
                throw RuntimeException("Exception caught while registering " + listener.javaClass.simpleName + ":" + method.name, e)
            }

            if (!unregister(l))
                throw IllegalStateException("Cannot remove method $method")
        }
        if (clazz.superclass != null)
            unregister0(listener, clazz.superclass)
    }


    private fun eventHandlerToListener(instance: Any, listener: Method, priority: Byte): EventListener<*> {
        if (listener.parameterCount != 1)
            throw IllegalArgumentException("Cannot derive EventListener from the argument method: bad argument number")

        listener.isAccessible = true
        val method = MethodHandles.lookup().unreflect(listener)

        val argument = listener.parameterTypes[0]

        return ReflectionEventListener(argument, priority, method, listener, instance)
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
        if (isBase(clazz)) {
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

    fun bake(clazz: Class<*>) {
        val baked = bake0(clazz)
        if (!baked.isEmpty()) {
            val b = baked.toTypedArray()
            byEventBaked[clazz] = b
        } else
            byEventBaked.remove(clazz)

        for (other in byListenerAndPriority.keys) {
            if (other != clazz && clazz.isAssignableFrom(other))
                bake(other)
        }
    }

    private fun bake0(clazz: Class<*>): List<EventListener<*>> {
        val handlers = byListenerAndPriority[clazz]
        val baked: MutableList<EventListener<*>>

        baked = if (handlers != null) {
            handlers.entries
                    .sortedBy { it.key.toInt() }
                    .flatMap { it.value }
                    .toMutableList()
        } else
            ArrayList()

        if (!isBase(clazz)) {
            val superClazz = clazz.superclass
            if (superClazz != CancellableEvent::class.java)
                baked.addAll(bake0(superClazz))
        }
        return baked
    }

    inner class ReflectionEventListener(clazz: Class<*>,
                                        priority: Byte,
                                        method: MethodHandle,
                                        val listener: Method,
                                        val instance: Any) : EventListener<Event>(clazz, priority) {
        val method: MethodHandle

        init {
            val met = method.bindTo(instance)
            this.method = met.asType(met.type().changeParameterType(0, Event::class.java))
        }

        override fun call(event: Event) {
            try {
                method.invokeExact(event)
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
