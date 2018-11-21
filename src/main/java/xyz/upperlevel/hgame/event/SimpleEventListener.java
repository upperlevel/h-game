package xyz.upperlevel.hgame.event;

import lombok.EqualsAndHashCode;

import java.util.function.Consumer;

@EqualsAndHashCode(callSuper = true)
public class SimpleEventListener<E extends Event> extends EventListener<E> {
    private final Consumer<E> consumer;

    public SimpleEventListener(Class<E> clazz, byte priority, Consumer<E> consumer) {
        super(clazz, priority);
        this.consumer = consumer;
    }

    public SimpleEventListener(Class<E> clazz, Consumer<E> consumer) {
        this(clazz, EventPriority.NORMAL, consumer);
    }

    @Override
    public void call(E event) {
        consumer.accept(event);
    }
}
