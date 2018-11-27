package xyz.upperlevel.hgame.network.discovery

import xyz.upperlevel.hgame.event.Event

import java.net.InetAddress

class DiscoveryResponseEvent(var ip: InetAddress, var nickname: String) : Event
