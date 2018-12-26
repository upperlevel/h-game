package xyz.upperlevel.hgame.world.entity

import xyz.upperlevel.hgame.network.Packet

data class EntityImpulsePacket(val entityId: Int,
                               val powerX: Float,
                               val powerY: Float,
                               val pointX: Float,
                               val pointY: Float) : Packet
