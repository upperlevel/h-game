package xyz.upperlevel.hgame.input

import xyz.upperlevel.hgame.network.Packet
import xyz.upperlevel.hgame.network.ProtocolId

@ProtocolId(1)
data class BehaviourChangePacket(val actorId: Int, val layerIndex: Int, val behaviour: String?) : Packet
