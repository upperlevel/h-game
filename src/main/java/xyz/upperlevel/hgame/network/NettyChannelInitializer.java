package xyz.upperlevel.hgame.network;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NettyChannelInitializer extends ChannelInitializer<SocketChannel> {
    private final Endpoint endpoint;

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline p = ch.pipeline();
        p.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
        p.addLast("frameEncoder", new LengthFieldPrepender(4));
        p.addLast("codec", new PayloadPacketCodec(endpoint.getJsonMapper()));
        p.addLast("executor", new NettyChannelEventCaller(endpoint));
    }
}
