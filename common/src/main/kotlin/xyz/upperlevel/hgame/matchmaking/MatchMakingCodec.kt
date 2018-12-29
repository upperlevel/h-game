package xyz.upperlevel.hgame.matchmaking

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToMessageCodec
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame
import io.netty.handler.codec.http.websocketx.WebSocketFrame
import java.lang.RuntimeException

class MatchMakingCodec : MessageToMessageCodec<WebSocketFrame, MatchMakingPacket>() {
    val json = jacksonObjectMapper()

    override fun encode(ctx: ChannelHandlerContext, msg: MatchMakingPacket, out: MutableList<Any>) {
        val name = MatchMakingPackets.packetToName[msg.javaClass]
                ?: throw IllegalStateException("Unknown MatchMakingPacket: ${msg.javaClass}")

        val data = json.writeValueAsString(msg)
        out.add(TextWebSocketFrame(name + "\n" + data))
    }

    override fun decode(ctx: ChannelHandlerContext, msg: WebSocketFrame, out: MutableList<Any>) {
        if (msg !is TextWebSocketFrame) return// Drop
        val text = msg.text()
        val separator = text.indexOf("\n")
        if (separator < 0) {
            throw RuntimeException("Cannot find name to content separator")
        }
        val name = text.substring(0, separator)
        val content = text.substring(separator + 1)

        val payloadClass = MatchMakingPackets.nameToPacket[name]
                ?: throw RuntimeException("Unknown packet name: $name")

        val payload = json.readValue(content, payloadClass)
        out.add(payload)
    }
}