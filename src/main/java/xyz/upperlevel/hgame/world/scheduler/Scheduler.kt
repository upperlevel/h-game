package xyz.upperlevel.hgame.world.scheduler

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

object Scheduler {
    private val tasksById = ConcurrentHashMap<Int, TaskHandle>()
    private val id = AtomicInteger(0)

    /**
     * Starts a new task.
     *
     * @param delay     the delay in milliseconds.
     * @param repeating may this task be repeated after it ended?
     * @return the id of the created task.
     */
    fun start(action: () -> Unit, delay: Long, repeating: Boolean = false): Int {
        val taskId = id.getAndIncrement()
        tasksById[taskId] = TaskHandle(taskId, action, delay, repeating)
        return taskId
    }

    /**
     * Starts a new task.
     *
     * @param delay     the delay in milliseconds.
     * @param repeating may this task be repeated after it ended?
     * @return the id of the created task.
     */
    fun start(action: Runnable, delay: Long, repeating: Boolean = false): Int {
        return start(action::run, delay, repeating)
    }

    fun update() {
        val currentTime = System.currentTimeMillis()
        for (handle in tasksById.values) {
            if (handle.isReady(currentTime)) {
                handle.run()
                if (!handle.isRepeating) {
                    tasksById.remove(handle.id)
                }
            }
        }
    }

    fun cancel(taskId: Int) {
        tasksById.remove(taskId)
    }
}
