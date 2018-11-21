package xyz.upperlevel.hgame.network;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NettyChannelInitializer extends ChannelInitializer<SocketChannel> {
    private final Endpoint endpoint;

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline p = ch.pipeline();
        p.addLast("payload-codec", new PayloadPacketCodec(endpoint.getJsonMapper()));
        p.addLast("executor", new NettyChannelEventCaller(endpoint));
    }
}
