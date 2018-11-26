package xyz.upperlevel.hgame.world.scheduler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Scheduler {
    private static final Map<Integer, TaskHandle> tasksById = new ConcurrentHashMap<>();
    private static AtomicInteger id = new AtomicInteger(0);

    /**
     * Starts a new task.
     *
     * @param delay     the delay in milliseconds.
     * @param repeating may this task be repeated after it ended?
     * @return the id of the created task.
     */
    public static int start(Runnable action, long delay, boolean repeating) {
        int taskId = id.getAndIncrement();
        tasksById.put(taskId, new TaskHandle(taskId, action, delay, repeating));
        return taskId;
    }

    /**
     * Starts a new delay task.
     *
     * @param delay the delay in milliseconds.
     * @return the id of the created task.
     */
    public static int start(Runnable action, long delay) {
        return start(action, delay, false); // by default not repeating
    }

    public static void update() {
        long currentTime = System.currentTimeMillis();
        for (TaskHandle handle : tasksById.values()) {
            if (handle.isReady(currentTime)) {
                handle.run();
                if (!handle.isRepeating()) {
                    tasksById.remove(handle.getId());
                }
            }
        }
    }

    public static void cancel(int taskId) {
        tasksById.remove(taskId);
    }

    private Scheduler() {
    }
}
