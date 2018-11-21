package xyz.upperlevel.hgame.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static xyz.upperlevel.hgame.event.EventListener.listener;

@RequiredArgsConstructor
public class EventChannel {
    private final Map<Class<?>, Map<Byte, Set<EventListener<?>>>> byListenerAndPriority = new HashMap<>();
    private final Map<Class<?>, EventListener<?>[]> byEventBaked = new HashMap<>();

    @Getter
    @Setter
    private Consumer<Throwable> exceptionHandler = (e) -> {
        if(e instanceof Exception)
            e.printStackTrace();
        else
            throw new IllegalStateException(e);
    };

    /**
     * Creates a listener with the given arguments (and the default priority) and registers it<br>
     * Listeners registered with this method cannot be unregistered
     * @param clazz the class of event to listen
     * @param consumer the listener
     * @param <E> the event to listen
     */
    public <E extends Event> void register(Class<E> clazz, Consumer<E> consumer) {
        register(listener(clazz, consumer));
    }

    /**
     * Creates a listener with the given arguments and registers it<br>
     * Listeners registered with this method cannot be unregistered
     * @param clazz the class of event to listen
     * @param consumer the listener
     * @param priority the priority of the listener
     * @param <E> the event to listen
     */
    public <E extends Event> void register(Class<E> clazz, Consumer<E> consumer, byte priority) {
        register(listener(clazz, consumer, priority));
    }

    public boolean call(CancellableEvent event) {
        call((Event)event);
        return !event.isCancelled();
    }

    public void register(Iterator<EventListener> events) {
        while(events.hasNext())
            register(events.next());
    }

    @SuppressWarnings("unchecked")
    public void register(EventListener<?> listener) {
        Map<Byte, Set<EventListener<?>>> handlers = byListenerAndPriority.computeIfAbsent(listener.getClazz(), k -> new HashMap<>());

        Set<EventListener<?>> l = handlers.computeIfAbsent(listener.getPriority(), k -> new HashSet<>());
        l.add(listener);
        bake(listener.getClazz());
    }

    public void unregister(Iterator<EventListener<?>> events) {
        while(events.hasNext())
            unregister(events.next());
    }

    public boolean unregister(EventListener<?> listener) {
        Map<Byte, Set<EventListener<?>>> handlers = byListenerAndPriority.get(listener.getClazz());
        if(handlers == null)
            return false;

        Set<? extends EventListener<?>> priorityMapped = handlers.get(listener.getPriority());
        if(priorityMapped == null)
            return false;
        if(priorityMapped.remove(listener)) {
            bake(listener.getClazz());
            return true;
        } else return false;
    }

    public void register(Listener listener) {
        register0(listener, listener.getClass());
    }

    protected void register0(Listener listener, Class<?> clazz) {
        Method[] methods = clazz.getDeclaredMethods();
        for(Method method : methods) {
            EventHandler handler = method.getAnnotation(EventHandler.class);

            if(handler == null)
                continue;
            EventListener l;
            try {
                l = eventHandlerToListener(listener, method, handler.priority());
            } catch (Exception e) {
                throw new RuntimeException("Exception caught while registering " + listener.getClass().getSimpleName() + ":" + method.getName(), e);
            }
            register(l);
        }
        if(clazz.getSuperclass() != null)
            register0(listener, clazz.getSuperclass());
    }


    public void unregister(Listener listener) {
        unregister0(listener, listener.getClass());
    }

    protected void unregister0(Listener listener, Class<?> clazz) {
        Method[] methods = clazz.getMethods();
        for(Method method : methods) {
            EventHandler handler = method.getAnnotation(EventHandler.class);
            if(handler == null)
                continue;
            EventListener l;
            try {
                l = eventHandlerToListener(listener, method, handler.priority());
            } catch (Exception e) {
                throw new RuntimeException("Exception caught while registering " + listener.getClass().getSimpleName() + ":" + method.getName(), e);
            }
            if(!unregister(l))
                throw new IllegalStateException("Cannot remove method " + method);
        }
        if(clazz.getSuperclass() != null)
            unregister0(listener, clazz.getSuperclass());
    }


    @SuppressWarnings("unchecked")
    protected EventListener eventHandlerToListener(Object instance, Method listener, byte priority) throws Exception {
        if (listener.getParameterCount() != 1)
            throw new IllegalArgumentException("Cannot derive EventListener from the argument method: bad argument number");

        listener.setAccessible(true);
        MethodHandle method = MethodHandles.lookup().unreflect(listener);

        Class<?> argument = listener.getParameterTypes()[0];

        return new ReflectionEventListener(argument, priority, method, listener, instance);
    }

    private void call0(Event event, Class<?> clazz) {
        if (clazz == null) return;
        EventListener<?>[] listeners = byEventBaked.get(clazz);

        if (listeners != null) {
            // If this class is in the map
            // Call the event
            for (EventListener listener : listeners) {
                listener.call(event);
            }
            //And exit
            return;
        }
        // If it wasn't in the map
        // And it can have event children
        if (isBase(clazz)) {
            call0(event, Event.class);
        } else {
            call0(event, clazz.getSuperclass());
        }
    }

    @SuppressWarnings("unchecked")
    public void call(Event event) {
        call0(event, event.getClass());
    }

    protected boolean isBase(Class<?> clazz) {
        if (clazz == null) return true;
        if (clazz.getSuperclass() == Object.class) return true;
        for(Class<?> i : clazz.getInterfaces()) {
            if (isBase(i)) {
                System.out.println("is base: " + clazz + " = true");
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public void bake(Class<?> clazz) {
        List<EventListener<?>> baked = bake0(clazz);
        if(!baked.isEmpty()) {
            EventListener<?>[] b = baked.toArray(new EventListener<?>[0]);
            byEventBaked.put(clazz, b);
        } else byEventBaked.remove(clazz);

        for(Class<?> other : byListenerAndPriority.keySet()) {
            if(other != clazz && clazz.isAssignableFrom(other))
                bake(other);
        }
    }

    protected List<EventListener<?>> bake0(Class<?> clazz) {
        Map<Byte, Set<EventListener<?>>> handlers = byListenerAndPriority.get(clazz);
        List<EventListener<?>> baked;

        if (handlers != null) {
            baked = handlers.entrySet().stream()
                    .sorted(Comparator.comparingInt(Map.Entry::getKey))
                    .flatMap(e -> e.getValue().stream())
                    .collect(Collectors.toList());
        } else baked = Collections.emptyList();

        if(!isBase(clazz)) {
            Class<?> superClazz = clazz.getSuperclass();
            if (superClazz != CancellableEvent.class)
                baked.addAll(bake0(superClazz));
        }
        return baked;
    }

    @Getter
    public class ReflectionEventListener extends EventListener {
        private final MethodHandle method;
        private final Method listener;
        private final Object instance;

        public ReflectionEventListener(Class<?> clazz, byte priority, MethodHandle method, Method listener, Object instance) {
            super(clazz, priority);
            method = method.bindTo(instance);
            this.method = method.asType(method.type().changeParameterType(0, Event.class));
            this.listener = listener;
            this.instance = instance;
        }

        @Override
        public void call(Event event) {
            try {
                method.invokeExact(event);
            } catch (Throwable t) {
                getExceptionHandler().accept(t);
            }
        }

        //Auto-created by lombok
        //Thanks lombok's authors for not fixing this "feature"! https://github.com/rzwitserloot/lombok/issues/1381
        public boolean equals(Object o) {
            if (o == this) return true;
            if (!(o instanceof EventChannel.ReflectionEventListener)) return false;
            final ReflectionEventListener other = (ReflectionEventListener) o;
            if (!super.equals(o)) return false;
            final Object this$listener = this.getListener();
            final Object other$listener = other.getListener();
            if (this$listener == null ? other$listener != null : !this$listener.equals(other$listener)) return false;
            final Object this$instance = this.getInstance();
            final Object other$instance = other.getInstance();
            if (this$instance == null ? other$instance != null : !this$instance.equals(other$instance)) return false;
            return true;
        }

        public int hashCode() {
            final int PRIME = 59;
            int result = 1;
            result = result * PRIME + super.hashCode();
            result = result * PRIME + (listener == null ? 43 : listener.hashCode());
            result = result * PRIME + (instance == null ? 43 : instance.hashCode());
            return result;
        }
    }
}
