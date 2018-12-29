package xyz.upperlevel.hgame.server

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import xyz.upperlevel.hgame.matchmaking.*
import java.lang.RuntimeException


class MatchMakingMessageHandler(
        val playerRegistry: PlayerRegistry,
        val lobbyRegistry: LobbyRegistry
) : SimpleChannelInboundHandler<MatchMakingPacket>() {

    lateinit var player: Player

    override fun handlerAdded(ctx: ChannelHandlerContext) {
        player = playerRegistry.onConnect(ctx.channel())
        super.handlerAdded(ctx)
    }

    override fun channelRead0(ctx: ChannelHandlerContext, packet: MatchMakingPacket) {
        // TODO: real packet listener
        // this is just an echo server

        if (player.name == null) {
            // Login needed
            when {
                packet !is LoginPacket -> ctx.writeAndFlush(OperationResultPacket("Login needed"))
                !playerRegistry.onLogin(player, packet.name) -> ctx.writeAndFlush(OperationResultPacket("Name already taken"))
                else -> {
                    player.name = packet.name
                    ctx.writeAndFlush(OperationResultPacket(null))
                }
            }
            return
        }

        when (packet) {
            is CreateLobbyPacket -> synchronized(lobbyRegistry) {
                if (lobbyRegistry.getFromName(packet.name) != null) {
                    ctx.writeAndFlush(OperationResultPacket("Lobby name already taken"))
                    return
                } else {
                    ctx.writeAndFlush(OperationResultPacket(null))
                }

                val lobby = lobbyRegistry.create(
                        player,
                        packet.name,
                        packet.private,
                        packet.password,
                        packet.maxPlayers
                )
                ctx.writeAndFlush(lobby.toInfoPacket())
            }
            is JoinLobbyPacket -> synchronized(lobbyRegistry) {
                val lobby = lobbyRegistry.getFromName(packet.name)

                if (lobby == null) {
                    ctx.writeAndFlush(OperationResultPacket("Lobby not found"))
                    return
                }
                val error = lobby.onJoin(player, packet.password)
                ctx.writeAndFlush(OperationResultPacket(error))

                if (error != null) {
                    ctx.writeAndFlush(lobby.toInfoPacket())
                }
            }
            is ReadyToPlayPacket -> {
                val lobby = player.lobby
                if (lobby == null) {
                    ctx.writeAndFlush(OperationResultPacket("No lobby joined"))
                    return
                }
                player.ready = packet.ready
                ctx.writeAndFlush(OperationResultPacket(null))
                lobby.refreshReady()
            }
            else -> throw RuntimeException("Invalid packet: $packet")
        }
    }

    override fun channelInactive(ctx: ChannelHandlerContext) {
        playerRegistry.onDisconnect(ctx.channel())
        super.channelInactive(ctx)
    }
}