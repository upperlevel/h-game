package xyz.upperlevel.hgame.network

import com.fasterxml.jackson.databind.ObjectMapper
import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufInputStream
import io.netty.buffer.ByteBufOutputStream
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageCodec
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

import java.io.InputStream
import java.io.OutputStream

import java.nio.charset.StandardCharsets.UTF_8

internal class PayloadPacketCodec(private val jsonMapper: ObjectMapper) : ByteToMessageCodec<PayloadPacket>() {
    override fun encode(ctx: ChannelHandlerContext, msg: PayloadPacket, out: ByteBuf) {
        val stream: OutputStream = ByteBufOutputStream(out)
        logger.debug("Writing packet: {}", msg)
        jsonMapper.writeValue(stream, msg)
    }

    @Throws(Exception::class)
    override fun decode(ctx: ChannelHandlerContext, `in`: ByteBuf, out: MutableList<Any>) {
        val stream: InputStream = ByteBufInputStream(`in`)
        val readIndex = `in`.readerIndex()
        val readBytes = `in`.readableBytes()

        if (logger.isDebugEnabled) {
            logger.debug("Received packet: " + `in`.toString(readIndex, readBytes, UTF_8))
        }

        val value: PayloadPacket
        try {
            value = jsonMapper.readValue<PayloadPacket>(stream, PayloadPacket::class.java)
        } catch (e: Exception) {
            throw RuntimeException("Exception reading packet: " + `in`.toString(readIndex, readBytes, UTF_8), e)
        }

        out.add(value)
    }

    companion object {
        val logger: Logger = LogManager.getLogger()
    }
}
