package xyz.upperlevel.hgame.network

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel

class Server(protocol: Protocol, val port: Int) : Endpoint(protocol, NetSide.MASTER) {
    override fun openAsync() {
        eventGroup = NioEventLoopGroup(1)

        val b = ServerBootstrap()
        b.group(eventGroup)
                .channel(NioServerSocketChannel::class.java)
                .option(ChannelOption.SO_BACKLOG, 100)
                .option(ChannelOption.TCP_NODELAY, true)
                //.handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(NettyChannelInitializer(this))

        val f = b.bind(port)

        try {
            f.sync()
        } catch (e: InterruptedException) {
            throw IllegalStateException(e)
        }
    }
}
