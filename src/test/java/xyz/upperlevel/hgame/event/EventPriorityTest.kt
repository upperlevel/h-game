package xyz.upperlevel.hgame.event

import org.junit.Assert.assertEquals
import org.junit.Test

class EventPriorityTest : Listener {

    private var calls: Int = 0

    class TestEvent : Event


    @EventHandler(priority = EventPriority.LOW)
    fun onTestLow(event: TestEvent) {
        assertEquals(0, calls++.toLong())
    }

    @EventHandler
    fun onTestNormal(event: TestEvent) {
        assertEquals(1, calls++.toLong())
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onTestHigh(event: TestEvent) {
        assertEquals(2, calls++.toLong())
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onTestMonitor(event: TestEvent) {
        assertEquals(3, calls++.toLong())
    }

    @Test
    fun test() {
        val manager = EventChannel()
        manager.register(this)
        manager.call(TestEvent())
        assertEquals(4, calls.toLong())
    }
}
