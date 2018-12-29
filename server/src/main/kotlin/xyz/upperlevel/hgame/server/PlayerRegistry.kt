package xyz.upperlevel.hgame.server

import io.netty.channel.Channel

class PlayerRegistry {
    private val playersByChannel = HashMap<Channel, Player>()

    fun onConnect(channel: Channel): Player {
        return playersByChannel.computeIfAbsent(channel) { Player(it) }
    }

    fun onDisconnect(channel: Channel) {
        val player = playersByChannel.remove(channel) ?: return

        player.lobby?.onQuit(player)
    }
}