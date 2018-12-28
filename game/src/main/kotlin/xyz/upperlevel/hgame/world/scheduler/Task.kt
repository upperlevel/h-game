package xyz.upperlevel.hgame.world.scheduler

abstract class Task : Runnable {
    var id: Int = 0
        private set

    val isCanceled: Boolean
        get() = id == -1

    private fun tryCancel(): Boolean {
        if (id != -1) {
            Scheduler.cancel(id)
            id = -1
            return true
        }
        return false
    }

    fun start(delay: Long, repeat: Boolean) {
        tryCancel()
        id = Scheduler.start(this, delay, repeat)
    }

    fun delay(delay: Long) {
        start(delay, false)
    }

    fun repeat(each: Long) {
        start(each, true)
    }

    fun cancel() {
        if (!tryCancel()) {
            throw IllegalStateException("This task has already been cancelled.")
        }
    }
}
