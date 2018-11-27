package xyz.upperlevel.hgame.network.discovery

import xyz.upperlevel.hgame.event.CancellableEvent

import java.net.InetAddress

class DiscoveryPairRequestEvent(val ip: InetAddress, val nickname: String) : CancellableEvent()
