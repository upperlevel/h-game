package xyz.upperlevel.hgame.world.entity

import xyz.upperlevel.hgame.network.Packet
import xyz.upperlevel.hgame.network.ProtocolId

@ProtocolId(0xE1_00000)
data class EntityImpulsePacket(val entityId: Int,
                               val powerX: Float,
                               val powerY: Float,
                               val pointX: Float,
                               val pointY: Float) : Packet
