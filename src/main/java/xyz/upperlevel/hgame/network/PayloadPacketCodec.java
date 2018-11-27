package xyz.upperlevel.hgame.network;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

@RequiredArgsConstructor
class PayloadPacketCodec extends ByteToMessageCodec<PayloadPacket> {
    public static final Logger logger = LogManager.getLogger();
    private final ObjectMapper jsonMapper;

    @Override
    protected void encode(ChannelHandlerContext ctx, PayloadPacket msg, ByteBuf out) throws Exception {
        OutputStream stream = new ByteBufOutputStream(out);
        logger.debug("Writing packet: {}", msg);
        jsonMapper.writeValue(stream, msg);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        InputStream stream = new ByteBufInputStream(in);
        int readIndex = in.readerIndex();
        int readBytes = in.readableBytes();

        if (logger.isDebugEnabled()) {
            logger.debug("Received packet: " + in.toString(readIndex, readBytes, UTF_8));
        }

        PayloadPacket value;
        try {
            value = jsonMapper.readValue(stream, PayloadPacket.class);
        } catch (Exception e) {
            throw new RuntimeException("Exception reading packet: " + in.toString(readIndex, readBytes, UTF_8), e);
        }
        out.add(value);
    }
}
