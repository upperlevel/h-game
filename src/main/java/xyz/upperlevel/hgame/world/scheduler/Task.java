package xyz.upperlevel.hgame.world.scheduler;

import lombok.Getter;

public abstract class Task implements Runnable {
    @Getter
    private int id;

    public Task() {
    }

    private boolean tryCancel() {
        if (id != -1) {
            Scheduler.cancel(id);
            id = -1;
            return true;
        }
        return false;
    }

    public void start(long delay, boolean repeat) {
        tryCancel();
        id = Scheduler.start(this, delay, repeat);
    }

    public void delay(long delay) {
        start(delay, false);
    }

    public void repeat(long each) {
        start(each, true);
    }

    public boolean isCanceled() {
        return id == -1;
    }

    public void cancel() {
        if (!tryCancel()) {
            throw new IllegalStateException("This task has already been cancelled.");
        }
    }
}
