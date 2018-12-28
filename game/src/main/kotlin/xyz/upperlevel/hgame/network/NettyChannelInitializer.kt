package xyz.upperlevel.hgame.network

import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.LengthFieldBasedFrameDecoder
import io.netty.handler.codec.LengthFieldPrepender


class NettyChannelInitializer(private val endpoint: Endpoint) : ChannelInitializer<SocketChannel>() {
    override fun initChannel(ch: SocketChannel) {
        val p = ch.pipeline()
        p.addLast("frameDecoder", LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4))
        p.addLast("frameEncoder", LengthFieldPrepender(4))
        p.addLast("codec", PayloadPacketCodec(endpoint.jsonMapper))
        p.addLast("executor", NettyChannelEventCaller(endpoint))
    }
}
