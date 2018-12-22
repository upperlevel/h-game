package xyz.upperlevel.hgame.world.entity

import xyz.upperlevel.hgame.network.Packet
import xyz.upperlevel.hgame.network.ProtocolId


@ProtocolId(0xE0_0001)
open class EntitySpawnPacket(var entityTypeId: String,
                             var entityId: Int,
                             var x: Float,
                             var y: Float,
                             var isFacingLeft: Boolean) : Packet

@ProtocolId(0xE0_0002)
class ThrowableEntitySpawnPacket(entityTypeId: String,
                                 entityId: Int,
                                 x: Float,
                                 y: Float,
                                 isFacingLeft: Boolean,
                                 var throwerEntityId: Int) : EntitySpawnPacket(entityTypeId, entityId, x, y, isFacingLeft)
