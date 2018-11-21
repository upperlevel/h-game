package xyz.upperlevel.hgame.networking;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Getter;

public class Client extends Endpoint {
    @Getter
    private final NettyChannelInitializer initializer = new NettyChannelInitializer(this);
    @Getter
    private final String host;
    @Getter
    private final int port;

    public Client(Protocol protocol, String host, int port) {
        super(protocol);
        this.host = host;
        this.port = port;
    }

    public void openAsync(boolean tcpNoDelay) {
        var eventGroup = new NioEventLoopGroup(1);

        setEventGroup(eventGroup);

        Bootstrap b = new Bootstrap();
        b.group(eventGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, tcpNoDelay)
                .handler(new NettyChannelInitializer(this));

        // Start the client.
        ChannelFuture f = b.connect(host, port);

        try {
            f.sync();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
