package xyz.upperlevel.hgame.network.discovery

import xyz.upperlevel.hgame.event.Event

import java.net.InetAddress

class DiscoveryPairResponseEvent(val ip: InetAddress,
                                 val nickname: String,
                                 val isSuccess: Boolean) : Event
