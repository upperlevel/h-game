package xyz.upperlevel.hgame.server

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame
import io.netty.handler.codec.http.websocketx.WebSocketFrame

class GamePacketRelayHandler(val player: Player) : SimpleChannelInboundHandler<WebSocketFrame>() {
    override fun channelRead0(ctx: ChannelHandlerContext, msg: WebSocketFrame) {
        if (msg is CloseWebSocketFrame) return
        player.lobby!!.players.forEach {
            if (it != player) {
                it.relayChannel?.writeAndFlush(msg.retain())
            }
        }
    }
}