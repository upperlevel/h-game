package tk.loryruta.hgame.scenario.scheduler;

import lombok.Getter;

public class TaskHandle {
    @Getter
    private final int id;

    @Getter
    private final Runnable action;

    @Getter
    private final boolean repeating;

    private long delay, callAt;

    public TaskHandle(int id, Runnable action, long delay, boolean repeating) {
        this.id = id;
        this.action = action;
        this.repeating = repeating;

        this.delay = delay;
        this.callAt = System.currentTimeMillis() + delay;
    }

    public boolean isReady(long time) {
        return time >= callAt;
    }

    public void run() {
        action.run();
        if (repeating) {
            callAt = System.currentTimeMillis() + delay;
        }
    }
}
