package xyz.upperlevel.hgame.event

import org.junit.Assert.assertEquals
import org.junit.Test

class ChannelInheritancePriorityTest {

    private var calls: Int = 0

    class TestEvent : Event


    fun parentLowPriority(event: TestEvent) {
        assertEquals(0, calls++.toLong())
    }

    fun childNormalPriority(event: TestEvent) {
        assertEquals(1, calls++.toLong())
    }

    fun parentHighPriority(event: TestEvent) {
        assertEquals(2, calls++.toLong())
    }

    fun childMonitor(event: TestEvent) {
        assertEquals(3, calls++.toLong())
    }

    @Test
    fun test() {
        val parent = EventChannel()
        val child = EventChannel()
        parent.register(TestEvent::class.java, this::parentLowPriority, EventPriority.LOW)
        child.register(TestEvent::class.java, this::childNormalPriority, EventPriority.NORMAL)
        child.parent = parent
        child.register(TestEvent::class.java, this::childMonitor, EventPriority.MONITOR)
        parent.register(TestEvent::class.java, this::parentHighPriority, EventPriority.HIGH)

        child.call(TestEvent())
        assertEquals(4, calls.toLong())
    }
}
