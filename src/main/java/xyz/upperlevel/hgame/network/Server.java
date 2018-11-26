package xyz.upperlevel.hgame.network;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.Getter;

public class Server extends Endpoint {
    @Getter
    private final NettyChannelInitializer initializer = new NettyChannelInitializer(this);
    @Getter
    private final int port;

    public Server(Protocol protocol, int port) {
        super(protocol, NetSide.MASTER);
        this.port = port;
    }

    @Override
    public void openAsync() {
        var eventGroup = new NioEventLoopGroup(1);
        setEventGroup(eventGroup);

        ServerBootstrap b = new ServerBootstrap();
        b.group(eventGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 100)
                .option(ChannelOption.TCP_NODELAY, true)
                //.handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(initializer);

        ChannelFuture f = b.bind(port);

        try {
            f.sync();
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }
}
