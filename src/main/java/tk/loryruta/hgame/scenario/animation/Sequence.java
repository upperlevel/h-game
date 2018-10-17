package tk.loryruta.hgame.scenario.animation;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Consumer;

public class Sequence {
    private static List<Entry<Trigger, Step>> incoming = new ArrayList<>();

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
        List<Entry<Trigger, Step>> incomingCopy = new ArrayList<>(incoming);
        for (Entry<Trigger, Step> entry : incomingCopy) {
            if (entry.getKey().get()) {
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

        public void next(Trigger trigger) {
            if (position >= steps.size() - 1) {
                return;
            }
            Step next = steps.get(position + 1);
            incoming.add(new AbstractMap.SimpleEntry<>(trigger, next));
        }

        public void next() {
            next(Trigger.NONE);
        }

        protected abstract void start();
    }
}
