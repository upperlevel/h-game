package xyz.upperlevel.hgame.input

import xyz.upperlevel.hgame.network.Packet
import xyz.upperlevel.hgame.network.ProtocolId

@ProtocolId(2)
data class TriggerInputActionPacket(var actorId: Int, var actionId: Int) : Packet
