package tk.loryruta.hgame.scenario.scheduler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scheduler {
    private static final Map<Integer, Task> tasksById = new HashMap<>();

    /**
     * Starts a new task.
     *
     * @param delay     the delay in milliseconds.
     * @param repeating may this task be repeated after it ended?
     * @return the id of the created task.
     */
    public static int start(Runnable action, long delay, boolean repeating) {
        int id = tasksById.size();
        tasksById.put(id, new Task(id, action, delay, repeating));
        return id;
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
        List<Task> useless = new ArrayList<>();
        // executes tasks
        for (Task task : tasksById.values()) {
            if (task.isReady()) {
                task.run();
                if (!task.isRepeating()) { // if the task does not repeat can be removed
                    useless.add(task);
                }
            }
        }
        // removes useless tasks
        useless.forEach(task -> cancel(task.getId()));
    }

    public static void cancel(int taskId) {
        tasksById.remove(taskId);
    }

    private Scheduler() {
    }
}
