package xyz.upperlevel.hgame.server

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.logging.LogLevel
import io.netty.handler.logging.LoggingHandler


object HGameServer {
    val port: Int = Integer.getInteger("port", 9080)

    val playerRegistry = PlayerRegistry()
    val lobbyRegistry = LobbyRegistry()

    @JvmStatic
    fun main(args: Array<String>) {
        val bossGroup = NioEventLoopGroup(1)
        val workerGroup = NioEventLoopGroup()
        try {
            val b = ServerBootstrap()
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel::class.java)
                    .handler(LoggingHandler(LogLevel.INFO))
                    .childHandler(ServerInitializer(playerRegistry, lobbyRegistry))

            val ch = b.bind(port).sync().channel()
            ch.closeFuture().sync()
        } finally {
            bossGroup.shutdownGracefully()
            workerGroup.shutdownGracefully()
        }
    }
}