package xyz.upperlevel.hgame.event

import org.junit.Assert.assertEquals
import org.junit.Test

class EventInheritanceTest : Listener {

    private var mails: Long = 0
    private var received: Long = 0
    private var sent: Long = 0

    open class MailEvent : Event

    class MailReceiveEvent : MailEvent()

    class MailSendEvent : MailEvent()

    private fun reset() {
        mails = 0
        received = 0
        sent = 0
    }

    @Test
    fun test() {
        val channel = EventChannel()
        channel.register(this)

        reset()
        channel.call(MailEvent())
        assertEquals(1, mails)
        assertEquals(0, received)
        assertEquals(0, sent)

        reset()
        channel.call(MailReceiveEvent())
        assertEquals(1, mails)
        assertEquals(1, received)
        assertEquals(0, sent)

        reset()
        channel.call(MailSendEvent())
        assertEquals(1, mails)
        assertEquals(0, received)
        assertEquals(1, sent)
    }

    @Test
    fun testOrderIndependence() {
        val channel = EventChannel()
        channel.register(MailReceiveEvent::class.java, this::onMailReceive)
        channel.register(MailEvent::class.java, this::onMail)
        channel.register(MailSendEvent::class.java, this::onMailSend)

        reset()
        channel.call(MailEvent())
        assertEquals(1, mails)
        assertEquals(0, received)
        assertEquals(0, sent)

        reset()
        channel.call(MailReceiveEvent())
        assertEquals(1, mails)
        assertEquals(1, received)
        assertEquals(0, sent)

        reset()
        channel.call(MailSendEvent())
        assertEquals(1, mails)
        assertEquals(0, received)
        assertEquals(1, sent)
    }

    @Test
    fun testPartialInheritanceIndependence() {
        val manager = EventChannel()
        //manager.register(MailReceiveEvent.class, this::onMailReceive);
        manager.register(MailEvent::class.java, this::onMail)
        manager.register(MailSendEvent::class.java, this::onMailSend)

        reset()
        manager.call(MailEvent())
        assertEquals(1, mails)
        assertEquals(0, received)
        assertEquals(0, sent)

        reset()
        manager.call(MailReceiveEvent())
        assertEquals(1, mails)
        //assertEquals(1, received);
        assertEquals(0, sent)
    }

    @EventHandler
    fun onMail(event: MailEvent) {
        mails++
    }

    @EventHandler
    fun onMailReceive(event: MailReceiveEvent) {
        received++
    }

    @EventHandler
    fun onMailSend(event: MailSendEvent) {
        sent++
    }
}
