package xyz.upperlevel.hgame.event

object EventPriority {
    const val LOWEST: Byte = -64
    const val LOW: Byte = -32
    const val NORMAL: Byte = 0
    const val HIGH: Byte = 32
    const val HIGHEST: Byte = 64

    /**
     * The MONITOR priority is the last priority to be called, in this priority no listener should change the event
     */
    const val MONITOR: Byte = 127
}
