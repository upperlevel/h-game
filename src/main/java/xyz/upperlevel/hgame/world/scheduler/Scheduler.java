package xyz.upperlevel.hgame.world.scheduler;

import java.util.HashMap;
import java.util.Map;

public class Scheduler {
    private static final Map<Integer, TaskHandle> tasksById = new HashMap<>();
    private static int id = 0;

    /**
     * Starts a new task.
     *
     * @param delay     the delay in milliseconds.
     * @param repeating may this task be repeated after it ended?
     * @return the id of the created task.
     */
    public static int start(Runnable action, long delay, boolean repeating) {
        tasksById.put(id, new TaskHandle(id, action, delay, repeating));
        return id++;
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
        Map<Integer, TaskHandle> tasksByIdCopy = new HashMap<>(tasksById);
        for (TaskHandle handle : tasksByIdCopy.values()) {
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
