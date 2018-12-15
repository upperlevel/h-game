package xyz.upperlevel.hgame.network

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler

class NettyChannelEventCaller(private val endpoint: Endpoint) : SimpleChannelInboundHandler<PayloadPacket>() {

    override fun channelActive(ctx: ChannelHandlerContext) {
        endpoint.onOpen(ctx.channel())
        /*if (!onOpen()) {
            ctx.close();
        }*/
    }

    override fun channelInactive(ctx: ChannelHandlerContext) {
        endpoint.onClose()
    }

    override fun channelRead0(ctx: ChannelHandlerContext, msg: PayloadPacket) {
        endpoint.onPacketReceive(msg)
    }
}
