package xyz.upperlevel.hgame.network

import io.netty.bootstrap.Bootstrap
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioSocketChannel
import java.net.InetAddress

class Client(protocol: Protocol,
             val host: InetAddress,
             val port: Int) : Endpoint(protocol, NetSide.SLAVE) {
    override fun openAsync() {
        val tcpNoDelay = true
        eventGroup = NioEventLoopGroup(1)

        val b = Bootstrap()
        b.group(eventGroup)
                .channel(NioSocketChannel::class.java)
                .option(ChannelOption.TCP_NODELAY, tcpNoDelay)
                .handler(NettyChannelInitializer(this))

        // Start the client.
        val f = b.connect(host, port)

        try {
            f.sync()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}
