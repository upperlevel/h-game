package xyz.upperlevel.hgame.networking;

import io.netty.channel.*;

public class NettyChannelEventCaller extends SimpleChannelInboundHandler<PayloadPacket> {
    private final Endpoint endpoint;

    public NettyChannelEventCaller(Endpoint endpoint) {
        this.endpoint = endpoint;
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) {
        endpoint.onOpen(ctx.channel());
        /*if (!onOpen()) {
            ctx.close();
        }*/
    }

    @Override
    public void channelInactive(final ChannelHandlerContext ctx) {
        endpoint.onClose();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, PayloadPacket msg) throws Exception {
        endpoint.onPacketReceive(msg);
    }
}
