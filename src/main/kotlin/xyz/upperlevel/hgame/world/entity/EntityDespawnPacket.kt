package xyz.upperlevel.hgame.world.entity

import xyz.upperlevel.hgame.network.Packet
import xyz.upperlevel.hgame.network.ProtocolId

@ProtocolId(4)
data class EntityDespawnPacket(val entityId: Int) : Packet
