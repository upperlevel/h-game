package xyz.upperlevel.hgame.world.entity

import xyz.upperlevel.hgame.network.Packet

data class EntityResetPacket(val entityId: Int, val data: Map<String, Any>) : Packet