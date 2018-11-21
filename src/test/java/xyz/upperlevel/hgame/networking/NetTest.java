package xyz.upperlevel.hgame.networking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.junit.Test;
import xyz.upperlevel.hgame.event.Event;
import xyz.upperlevel.hgame.event.EventListener;
import xyz.upperlevel.hgame.networking.events.ConnectionCloseEvent;
import xyz.upperlevel.hgame.networking.events.ConnectionOpenEvent;

import java.util.concurrent.LinkedBlockingQueue;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;

public class NetTest {
    @Data// getText and setText required to Jackson
    @ToString// To get nice exceptions
    @AllArgsConstructor// For dev. simplicity
    @NoArgsConstructor// Empty constructor required by Jackson
    @ProtocolId(1)
    public static class ExamplePacket implements Packet {
        private String text;
    }

    @Data
    @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    @ProtocolId(2)
    public static class SecondExamplePacket implements Packet {
        private int aSimpleText;
    }

    @Test
    public void simpleTest() throws InterruptedException {
        Protocol protocol = new Protocol();
        protocol.add(ExamplePacket.class);
        protocol.add(SecondExamplePacket.class);

        Server server = new Server(protocol, 12345);
        Client client = new Client(protocol, "localhost", 12345);

        var serverEvents = new LinkedBlockingQueue<Event>();
        var clientEvents = new LinkedBlockingQueue<Event>();


        server.getEvents().register(EventListener.listener(Event.class, e -> {
            serverEvents.add(e);
        }));
        client.getEvents().register(EventListener.listener(Event.class, e -> {
            clientEvents.add(e);
        }));

        server.openAsync();
        client.openAsync(true);

        assertThat(serverEvents.take(), instanceOf(ConnectionOpenEvent.class));
        assertThat(clientEvents.take(), instanceOf(ConnectionOpenEvent.class));

        client.send(new ExamplePacket("Lol"));
        assertThat(serverEvents.take(), equalTo(new ExamplePacket("Lol")));

        server.send(new SecondExamplePacket(42));
        assertThat(clientEvents.take(), equalTo(new SecondExamplePacket(42)));

        client.close();

        assertThat(serverEvents.take(), instanceOf(ConnectionCloseEvent.class));
        assertThat(clientEvents.take(), instanceOf(ConnectionCloseEvent.class));

        server.close();
    }
}
