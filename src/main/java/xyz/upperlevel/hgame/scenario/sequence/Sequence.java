package xyz.upperlevel.hgame.scenario.sequence;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Sequence {
    private static List<Sequence> sequences = new ArrayList<>();

    private Map<Step, Trigger> incomingSteps = new HashMap<>();
    private List<Step> steps = new ArrayList<>();
    private List<Sequence> children = new ArrayList<>();

    private Sequence() {
    }

    /**
     * Appends an action to the current sequence.
     * To continue the sequence, the action is supposed to call the function {@link Sequence.Step#next(Trigger)}.
     */
    public Sequence append(Consumer<Step> action) {
        steps.add(new Step(steps.size()) {
            @Override
            public void start() {
                action.accept(this);
            }
        });
        return this;
    }

    public Sequence act(Runnable action) {
        append(step -> {
            action.run();
            step.next(Trigger.NONE);
        });
        return this;
    }

    public Sequence delay(long delay) {
        append(step -> step.next(Trigger.sleep(delay)));
        return this;
    }

    private void issueRepeat(Step root, Consumer<Step> action, long each) {
        append(step -> {
            action.accept(root);
            if (!root.isNextCalled()) {
                delay(each);
                issueRepeat(root, action, each);
                step.next(Trigger.NONE);
            } else {
                dismiss();
            }
        });
    }

    public Sequence repeat(Consumer<Step> action, long each) {
        append(root -> {
            // We create a sub-sequence that will handle the
            // repeating steps. If `next` is called within
            // the `action`, the first level sequence skips to the next step
            // and the inner sequence is dismissed.
            Sequence inner = new Sequence();
            inner.issueRepeat(root, action, each);
            children.add(inner);
            inner.play();
        });
        return this;
    }

    public Sequence repeat(BiConsumer<Step, Integer> action, long each, int times) {
        AtomicInteger wrapped = new AtomicInteger();
        repeat(step -> {
            action.accept(step, wrapped.get());
            if (wrapped.incrementAndGet() >= times) {
                step.next(Trigger.NONE);
            }
        }, each);
        return this;
    }

    public void update() {
        for (Entry<Step, Trigger> incomingStep : new HashMap<>(incomingSteps).entrySet()) {
            if (incomingStep.getValue().get()) {
                Step step = incomingStep.getKey();
                step.start();
                incomingSteps.remove(incomingStep.getKey());
            }
        }
        children.forEach(Sequence::update);
    }

    public void play() {
        if (steps.size() > 0) {
            steps.get(0).start();
        }
    }

    public void dismiss() {
        incomingSteps.clear();
        children.forEach(Sequence::dismiss);
        sequences.remove(this);
    }

    public abstract class Step {
        @Getter
        private boolean nextCalled;
        private int position;

        public Step(int position) {
            this.position = position;
        }

        public void next(Trigger trigger) {
            nextCalled = true;
            if (position >= steps.size() - 1) {
                return;
            }
            Step next = steps.get(position + 1);
            incomingSteps.put(next, trigger);
        }

        public abstract void start();
    }

    public static Sequence create() {
        Sequence sequence = new Sequence();
        sequences.add(sequence);
        return sequence;
    }

    public static void updateAll() {
        for (Sequence sequence : sequences) {
            sequence.update();
        }
    }

    public static void dismissAll() {
        for (Sequence sequence : sequences) {
            sequence.dismiss();
        }
    }
}
