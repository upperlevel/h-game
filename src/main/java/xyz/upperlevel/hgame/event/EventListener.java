package xyz.upperlevel.hgame.event;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.function.Consumer;

@EqualsAndHashCode
@Data
public abstract class EventListener<E extends Event> {
    private final Class<?> clazz;
    private final byte priority;

    public EventListener(Class<?> clazz, byte priority) {
        this.clazz = clazz;
        this.priority = priority;
    }

    public EventListener(Class<?> clazz) {
        this(clazz, EventPriority.NORMAL);
    }

    public abstract void call(E event);

    public static <E extends Event> EventListener<E> listener(Class<E> clazz, Consumer<E> consumer, byte priority) {
        return new SimpleEventListener<>(clazz, priority, consumer);
    }

    public static <E extends Event> EventListener<E> listener(Class<E> clazz, Consumer<E> consumer) {
        return new SimpleEventListener<>(clazz, EventPriority.NORMAL, consumer);
    }

    public static <E extends Event> EventListener<E> of(Class<E> clazz, Consumer<E> consumer, byte priority) {
        return new SimpleEventListener<>(clazz, priority, consumer);
    }

    public static <E extends Event> EventListener<E> of(Class<E> clazz, Consumer<E> consumer) {
        return new SimpleEventListener<>(clazz, EventPriority.NORMAL, consumer);
    }
}
