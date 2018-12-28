package xyz.upperlevel.hgame.world.scheduler

class TaskHandle(val id: Int,
                 val action: () -> Unit,
                 private val delay: Long,
                 val isRepeating: Boolean) {
    private var callAt: Long = 0

    init {
        this.callAt = System.currentTimeMillis() + delay
    }

    fun isReady(time: Long): Boolean {
        return time >= callAt
    }

    fun run() {
        action()
        if (isRepeating) {
            callAt = System.currentTimeMillis() + delay
        }
    }
}
