package xyz.upperlevel.hgame.event

import org.junit.Assert.assertEquals
import org.junit.Test

class EventRegistrationTest {

    class TestEvent : Event

    class OtherEvent : Event

    class ClassEventListener : Listener {

        @EventHandler
        fun onTest(event: TestEvent) {
            testCalls++
        }

        @EventHandler
        fun onOhter(event: OtherEvent) {
            otherCalls++
        }
    }

    class DirectEventListener : EventListener<TestEvent>(TestEvent::class.java) {

        override fun call(event: TestEvent) {
            testCalls++
        }
    }

    fun reset() {
        testCalls = 0
        otherCalls = 0
    }

    @Test
    fun testRegisterUnregister() {
        val channel = EventChannel()

        val classListener = ClassEventListener()
        channel.register(classListener)

        val directListener = DirectEventListener()
        channel.register(directListener)

        reset()
        channel.call(TestEvent())
        assertEquals(2, testCalls.toLong())
        assertEquals(0, otherCalls.toLong())

        reset()
        channel.call(OtherEvent())
        assertEquals(0, testCalls.toLong())
        assertEquals(1, otherCalls.toLong())

        channel.unregister(directListener)

        reset()
        channel.call(TestEvent())
        assertEquals(1, testCalls.toLong())
        assertEquals(0, otherCalls.toLong())

        reset()
        channel.call(OtherEvent())
        assertEquals(0, testCalls.toLong())
        assertEquals(1, otherCalls.toLong())

        channel.unregister(classListener)

        reset()
        channel.call(TestEvent())
        assertEquals(0, testCalls.toLong())
        assertEquals(0, otherCalls.toLong())

        reset()
        channel.call(OtherEvent())
        assertEquals(0, testCalls.toLong())
        assertEquals(0, otherCalls.toLong())
    }

    companion object {
        private var testCalls: Int = 0
        private var otherCalls: Int = 0
    }
}
