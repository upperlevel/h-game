package xyz.upperlevel.hgame.server

import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.http.HttpObjectAggregator
import io.netty.handler.codec.http.HttpServerCodec
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler


class ServerInitializer(
        val playerRegistry: PlayerRegistry,
        val lobbyRegistry: LobbyRegistry) : ChannelInitializer<SocketChannel>() {
    override fun initChannel(channel: SocketChannel) {
        val pipeline = channel.pipeline()

        pipeline.addLast(HttpServerCodec())
                .addLast(HttpObjectAggregator(65536))
                .addLast(WebSocketServerCompressionHandler())
                .addLast(WebSocketServerProtocolHandler("/", null, true))
                .addLast(ConnectionHandshakeHandler(playerRegistry, lobbyRegistry))
    }
}