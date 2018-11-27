package xyz.upperlevel.hgame

import xyz.upperlevel.hgame.input.TriggerInputActionPacket
import xyz.upperlevel.hgame.network.Protocol
import xyz.upperlevel.hgame.world.entity.EntitySpawnPacket

object GameProtocol {
    const val GAME_PORT = 23432
    val PROTOCOL = Protocol.builder()
            .add(TriggerInputActionPacket::class.java)
            .add(EntitySpawnPacket::class.java)
            .build()
}
