package xyz.upperlevel.hgame

import xyz.upperlevel.hgame.input.BehaviourChangePacket
import xyz.upperlevel.hgame.network.Protocol
import xyz.upperlevel.hgame.world.character.PlayerJumpPacket
import xyz.upperlevel.hgame.world.entity.*

object GameProtocol {
    const val GAME_PORT = 23432
    val PROTOCOL = Protocol.builder()
            .add(EntitySpawnPacket::class.java)
            .add(ThrowableEntitySpawnPacket::class.java)
            .add(PlayerSpawnPacket::class.java)
            .add(EntityDespawnPacket::class.java)
            .add(EntityImpulsePacket::class.java)
            .add(EntityResetPacket::class.java)

            .add(BehaviourChangePacket::class.java)
            .add(PlayerJumpPacket::class.java)
            .build()
}
