package xyz.upperlevel.hgame.network;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import xyz.upperlevel.hgame.event.EventChannel;
import xyz.upperlevel.hgame.network.events.ConnectionCloseEvent;
import xyz.upperlevel.hgame.network.events.ConnectionOpenEvent;

public class Endpoint {
    @Getter
    private final Protocol protocol;
    @Getter
    private final ObjectMapper jsonMapper;
    @Getter
    private EventChannel events = new EventChannel();

    @Getter
    private Channel channel = null;

    @Setter(AccessLevel.PROTECTED)
    private EventLoopGroup eventGroup;

    public Endpoint(Protocol protocol) {
        this.protocol = protocol;
        jsonMapper = new ObjectMapper();

        SimpleModule module = new SimpleModule();
        module.addDeserializer(PayloadPacket.class, new PayloadPacketDeserializer(protocol));

        jsonMapper.registerModule(module);
    }


    public void onPacketReceive(PayloadPacket msg) {
        events.call(msg.getData());
    }

    public void onOpen(Channel channel) {
        this.channel = channel;
        events.call(new ConnectionOpenEvent());
    }

    public void onClose() {
        events.call(new ConnectionCloseEvent());
        this.channel = null;
    }

    public boolean isConnected() {
        return channel != null;
    }

    public void send(Packet packet) {
        var id = protocol.fromClass(packet.getClass())
                .orElseThrow(() -> new IllegalArgumentException("Packet not registered: " + packet.getClass()));
        channel.writeAndFlush(new PayloadPacket(id, packet));
    }

    public void close() throws InterruptedException {
        if (!isConnected()) return;
        getChannel().close().sync();
    }

    public void closeGracefully() throws InterruptedException {
        close();
        eventGroup.shutdownGracefully();
    }
}
