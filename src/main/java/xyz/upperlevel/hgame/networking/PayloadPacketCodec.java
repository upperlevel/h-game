package xyz.upperlevel.hgame.networking;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import lombok.RequiredArgsConstructor;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

@RequiredArgsConstructor
class PayloadPacketCodec extends ByteToMessageCodec<PayloadPacket> {
    private final ObjectMapper jsonMapper;

    @Override
    protected void encode(ChannelHandlerContext ctx, PayloadPacket msg, ByteBuf out) throws Exception {
        OutputStream stream = new ByteBufOutputStream(out);
        jsonMapper.writeValue(stream, msg);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        InputStream stream = new ByteBufInputStream(in);
        var value = jsonMapper.readValue(stream, PayloadPacket.class);
        out.add(value);
    }
}
