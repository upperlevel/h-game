package xyz.upperlevel.hgame.scenario;

import xyz.upperlevel.hgame.scenario.character.Actor;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Supplier;

public class Event {
    private static final List<Entry<Supplier<Boolean>, Runnable>> events = new ArrayList<>();

    public static void watch(Supplier<Boolean> trigger, Runnable action) {
        events.add(new AbstractMap.SimpleEntry<>(trigger, action));
    }

    public static void update() {
        List<Entry<Supplier<Boolean>, Runnable>> eventsCopy = new ArrayList<>(events);
        for (Entry<Supplier<Boolean>, Runnable> entry : eventsCopy) {
            if (entry.getKey().get()) {
                entry.getValue().run();
                events.remove(entry);
            }
        }
    }

    private Event() {
    }

    public static Supplier<Boolean> greaterX(Actor actor, float x) {
        return () -> actor.getX() >= x;
    }

    public static Supplier<Boolean> lowerX(Actor actor, float x) {
        return () -> actor.getX() <= x;
    }
}
