package xyz.upperlevel.hgame.server

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame
import io.netty.handler.codec.http.websocketx.WebSocketFrame
import xyz.upperlevel.hgame.matchmaking.MatchMakingCodec

class ConnectionHandshakeHandler(
        val playerRegistry: PlayerRegistry,
        val lobbyRegistry: LobbyRegistry
): SimpleChannelInboundHandler<WebSocketFrame>() {

    fun writeHandshakeError(ctx: ChannelHandlerContext, error: String) {
        ctx.writeAndFlush(TextWebSocketFrame(error))
    }

    override fun channelRead0(ctx: ChannelHandlerContext, msg: WebSocketFrame) {
        if (msg !is TextWebSocketFrame) return// drop binary frames

        val text = msg.text().lines()

        if (text.size != 2) {
            writeHandshakeError(ctx, "Invalid header, expected version and connection purpose")
            ctx.close()
            return
        }

        if (!text[0].startsWith(VERSION_PREFIX)) {
            writeHandshakeError(ctx, "No protocol version provided")
            ctx.close()
            return
        }

        val version = text[0].substring(VERSION_PREFIX.length).trim()
        if (version != VERSION) {
            writeHandshakeError(ctx, "Invalid protocol version, server is $VERSION")
            ctx.close()
            return
        }

        // check the purpose of the connection
        // and change the pipeline accordingly
        val args = text[1].split(" ")

        when (args[0].toLowerCase()) {
            "matchmaking" -> initMatchMaking(ctx)
            "play" -> initPlay(ctx, args)
            else -> {
                writeHandshakeError(ctx, "Invalid purpose")
                ctx.close()
                return
            }
        }
    }

    fun initMatchMaking(ctx: ChannelHandlerContext) {
        writeHandshakeError(ctx, "ok")
        ctx.pipeline()
                .addLast(MatchMakingCodec())
                .addLast(MatchMakingMessageHandler(playerRegistry, lobbyRegistry))
                .remove(this)
    }

    fun initPlay(ctx: ChannelHandlerContext, args: List<String>) {
        if (args.size != 2) {
            writeHandshakeError(ctx, "No token found")
            ctx.close()
            return
        }
        val token = args[1]
        // In this implementation the token is just the player name
        // this is not secure at all but as a quick implementation it'll work i hope

        val player = playerRegistry.getByName(token)

        if (player == null || player.relayChannel != null) {
            writeHandshakeError(ctx, "Invalid token")
            ctx.close()
            return
        }

        player.relayChannel = ctx.channel()
        writeHandshakeError(ctx, "ok")

        ctx.pipeline()
                .addLast(GamePacketRelayHandler(player))
                .remove(this)
    }

    companion object {
        const val VERSION = "A0.1"

        const val VERSION_PREFIX = "version "
    }
}