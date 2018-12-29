package xyz.upperlevel.hgame.server

import io.netty.channel.Channel

class PlayerRegistry {
    private val playersByChannel = HashMap<Channel, Player>()
    private val playersByName = HashMap<String, Player>()

    fun onConnect(channel: Channel): Player {
        return playersByChannel.computeIfAbsent(channel) { Player(it) }
    }

    fun onLogin(player: Player, name: String): Boolean {
        return playersByName.putIfAbsent(name, player) != null
    }

    fun onDisconnect(channel: Channel) {
        val player = playersByChannel.remove(channel) ?: return

        playersByName.remove(player.name)

        player.lobby?.onQuit(player)
    }

    fun getByName(name: String): Player? {
        return playersByName[name]
    }
}