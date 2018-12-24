package xyz.upperlevel.hgame.world.entity

import xyz.upperlevel.hgame.network.Packet
import xyz.upperlevel.hgame.network.ProtocolId

data class EntityDespawnPacket(val entityId: Int) : Packet
