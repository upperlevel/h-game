package xyz.upperlevel.hgame.server

import io.netty.channel.Channel

class Player(val channel: Channel) {
    var name: String? = null
    var lobby: Lobby? = null
    var ready = false
}