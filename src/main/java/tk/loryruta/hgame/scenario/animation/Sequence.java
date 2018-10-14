package tk.loryruta.hgame.scenario.animation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.Consumer;

public class Sequence {
    private static List<Entry<Waiter, Step>> incoming = new ArrayList<>();

    private List<Step> steps = new ArrayList<>();

    public Sequence() {
    }

    public Sequence append(Consumer<Step> action) {
        steps.add(new Step(steps.size()) {
            @Override
            protected void start() {
                action.accept(this);
            }
        });
        return this;
    }

    public void play() {
        if (steps.size() > 0) {
            steps.get(0).start();
        }
    }

    public static void update() {
        List<Entry<Waiter, Step>> incomingCopy = new ArrayList<>(incoming);
        for (Entry<Waiter, Step> entry : incomingCopy) {
            if (entry.getKey().test()) {
                entry.getValue().start();
                incoming.remove(entry);
            }
        }
    }

    public static void dispose() {
        incoming.clear();
    }

    public abstract class Step {
        private int position;

        public Step(int position) {
            this.position = position;
        }

        public void next(Waiter waiter) {
            if (position >= steps.size() - 1) {
                return;
            }
            Step next = steps.get(position + 1);
            incoming.add(new AbstractMap.SimpleEntry<>(waiter, next));
        }

        public void next() {
            next(Waiter.NONE);
        }

        protected abstract void start();
    }

    public interface Waiter {
        Waiter NONE = () -> true;
        Waiter ENTER_KEY = () -> Gdx.input.isKeyJustPressed(Input.Keys.ENTER);

        boolean test();

        static Waiter sleep(long duration) {
            long now = System.currentTimeMillis();
            return () -> System.currentTimeMillis() >= (now + duration);
        }
    }
}
