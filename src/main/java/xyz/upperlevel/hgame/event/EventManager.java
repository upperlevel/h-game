package xyz.upperlevel.hgame.event;

import java.util.function.Consumer;

import static xyz.upperlevel.hgame.event.EventListener.listener;

public class EventManager extends GeneralEventManager<Event> {

    public EventManager() {
        super(Event.class);
    }

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
        super.call(event);
        return !event.isCancelled();
    }
}
