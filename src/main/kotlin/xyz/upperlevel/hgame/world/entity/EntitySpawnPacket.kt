package xyz.upperlevel.hgame.world.entity

import xyz.upperlevel.hgame.network.Packet
import xyz.upperlevel.hgame.network.ProtocolId


@ProtocolId(0)
data class EntitySpawnPacket(var entityTypeId: Int,
                             var x: Float,
                             var y: Float,
                             var isFacingLeft: Boolean,
                             /**
                              * This is true only if the packet is a response to the client's request
                              * When the client sends a request to the server the value is ignored
                              */
                             var isConfirmation: Boolean) : Packet
