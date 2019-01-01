package xyz.upperlevel.hgame.server

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import xyz.upperlevel.hgame.matchmaking.*


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
                packet !is LoginPacket -> ctx.writeAndFlush(OperationResultPacket("Login needed."))
                !playerRegistry.onLogin(player, packet.name) -> ctx.writeAndFlush(OperationResultPacket("Name already taken."))
                else -> {
                    ctx.writeAndFlush(OperationResultPacket(null))
                }
            }
            return
        }

        when (packet) {
            is PlayerLobbyInfoChangePacket -> synchronized(lobbyRegistry) {
                player.character = packet.character
                player.ready = packet.ready
                // TODO: if player ready but no lobby joined then join a casual match

                ctx.writeAndFlush(OperationResultPacket(null))
                player.lobby?.broadcastLobbyInfo()
            }
            is InvitePacket -> synchronized(lobbyRegistry) {
                val packetPlayer = playerRegistry.getByName(packet.player)
                if (packetPlayer == null) {
                    ctx.writeAndFlush(OperationResultPacket("Player not found"))
                    return
                }
                when (packet.type) {
                    InvitePacketType.INVITE_PLAYER -> {
                        player.sendInvite(packetPlayer)
                        // This can't fail (for now)
                        ctx.writeAndFlush(OperationResultPacket(null))
                        Unit
                    }
                    InvitePacketType.ACCEPT_INVITE -> {
                        val error = player.acceptInvite(packetPlayer, lobbyRegistry)
                        ctx.writeAndFlush(OperationResultPacket(error))
                        if (error == null) {
                            // Notify the other players of the new friend in the lobby
                            player.lobby?.broadcastLobbyInfo()
                        }
                    }
                    else -> {
                        ctx.writeAndFlush(OperationResultPacket("Invalid invite type"))
                        return
                    }
                }
            }
            else -> ctx.writeAndFlush(OperationResultPacket("Invalid packet type: ${packet.javaClass}"))
        }
    }

    override fun channelInactive(ctx: ChannelHandlerContext) {
        player.invalidateSentInvites()
        playerRegistry.onDisconnect(ctx.channel())
        super.channelInactive(ctx)
    }
}