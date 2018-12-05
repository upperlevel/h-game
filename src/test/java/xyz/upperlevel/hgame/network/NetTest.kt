package xyz.upperlevel.hgame.network

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import xyz.upperlevel.hgame.event.Event
import xyz.upperlevel.hgame.event.EventListener
import xyz.upperlevel.hgame.network.events.ConnectionCloseEvent
import xyz.upperlevel.hgame.network.events.ConnectionOpenEvent
import java.net.InetAddress
import java.net.UnknownHostException
import java.util.concurrent.LinkedBlockingQueue

class NetTest {
    @ProtocolId(1)
    data class ExamplePacket(val text: String) : Packet

    @ProtocolId(2)
    data class SecondExamplePacket(val simpleText: Int) : Packet

    @Test
    @Throws(InterruptedException::class, UnknownHostException::class)
    fun simpleTest() {
        val protocol = Protocol.builder()
                .add(ExamplePacket::class.java)
                .add(SecondExamplePacket::class.java)
                .build()

        val server = Server(protocol, 12345)
        val client = Client(protocol, InetAddress.getLocalHost(), 12345)

        val serverEvents = LinkedBlockingQueue<Event>()
        val clientEvents = LinkedBlockingQueue<Event>()


        server.events.register(EventListener.listener(Event::class.java, { e -> serverEvents.add(e) }))
        client.events.register(EventListener.listener(Event::class.java, { e -> clientEvents.add(e) }))

        server.openAsync()
        client.openAsync()

        assertThat(serverEvents.take(), instanceOf(ConnectionOpenEvent::class.java))
        assertThat(clientEvents.take(), instanceOf(ConnectionOpenEvent::class.java))

        client.send(ExamplePacket("Lol"))
        assertThat(serverEvents.take(), equalTo<Event>(ExamplePacket("Lol")))

        server.send(SecondExamplePacket(42))
        assertThat(clientEvents.take(), equalTo<Event>(SecondExamplePacket(42)))

        client.close()

        assertThat(serverEvents.take(), instanceOf(ConnectionCloseEvent::class.java))
        assertThat(clientEvents.take(), instanceOf(ConnectionCloseEvent::class.java))

        server.close()
    }
}
