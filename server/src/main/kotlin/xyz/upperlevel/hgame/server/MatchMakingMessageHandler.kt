package xyz.upperlevel.hgame.server

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import xyz.upperlevel.hgame.matchmaking.MatchMakingPacket


class MatchMakingMessageHandler(
        val playerRegistry: PlayerRegistry,
        val roomRegistry: RoomRegistry) : SimpleChannelInboundHandler<MatchMakingPacket>() {

    override fun channelActive(ctx: ChannelHandlerContext) {
        super.channelActive(ctx)
        playerRegistry.onConnect(ctx.channel())
    }

    override fun channelRead0(ctx: ChannelHandlerContext, packet: MatchMakingPacket) {
        // TODO: real packet listener
        // this is just an echo server
        ctx.writeAndFlush(packet)
    }

    override fun channelInactive(ctx: ChannelHandlerContext) {
        playerRegistry.onDisconnect(ctx.channel())
        super.channelInactive(ctx)
    }
}