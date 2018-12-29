package xyz.upperlevel.hgame.server

import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler
import io.netty.handler.codec.http.HttpObjectAggregator
import io.netty.handler.codec.http.HttpServerCodec
import xyz.upperlevel.hgame.matchmaking.MatchMakingCodec


class ServerInitializer(
        val playerRegistry: PlayerRegistry,
        val roomRegistry: RoomRegistry) : ChannelInitializer<SocketChannel>() {
    override fun initChannel(channel: SocketChannel) {
        val pipeline = channel.pipeline()

        pipeline.addLast(HttpServerCodec())
                .addLast(HttpObjectAggregator(65536))
                .addLast(WebSocketServerCompressionHandler())
                .addLast(WebSocketServerProtocolHandler("/", null, true))
                .addLast(MatchMakingCodec())
                .addLast(MatchMakingMessageHandler(playerRegistry, roomRegistry))
    }
}