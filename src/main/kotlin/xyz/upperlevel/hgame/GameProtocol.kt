package xyz.upperlevel.hgame

import xyz.upperlevel.hgame.input.BehaviourChangePacket
import xyz.upperlevel.hgame.network.Protocol
import xyz.upperlevel.hgame.world.entity.EntityDespawnPacket
import xyz.upperlevel.hgame.world.character.PlayerJumpPacket
import xyz.upperlevel.hgame.world.entity.EntitySpawnPacket
import xyz.upperlevel.hgame.world.entity.ThrowableEntitySpawnPacket

object GameProtocol {
    const val GAME_PORT = 23432
    val PROTOCOL = Protocol.builder()
            .add(EntitySpawnPacket::class.java)
            .add(ThrowableEntitySpawnPacket::class.java)
            .add(EntityDespawnPacket::class.java)


            .add(BehaviourChangePacket::class.java)
            .add(PlayerJumpPacket::class.java)
            .build()
}
