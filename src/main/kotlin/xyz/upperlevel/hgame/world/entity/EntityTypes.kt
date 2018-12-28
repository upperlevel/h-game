package xyz.upperlevel.hgame.world.entity

import xyz.upperlevel.hgame.world.player.impl.Elisa
import xyz.upperlevel.hgame.world.entity.impl.Mikrotik
import xyz.upperlevel.hgame.world.player.impl.Mixter
import xyz.upperlevel.hgame.world.entity.impl.Poison
import xyz.upperlevel.hgame.world.player.PlayerEntityType
import xyz.upperlevel.hgame.world.player.impl.Santy
import java.util.*


object EntityTypes {
    private val byId: MutableMap<String, EntityType> = HashMap()

    private fun register(entityType: EntityType) {
        byId[entityType.id] = entityType
    }

    operator fun get(id: String): EntityType? {
        return byId[id]
    }

    val MIKROTIK = Mikrotik().also { register(it) }
    val MIXTER   = Mixter().also { register(it) }
    val SANTY    = Santy().also { register(it) }
    val ELISA    = Elisa().also { register(it) }
    val POISON   = Poison().also { register(it) }

    val playable: List<PlayerEntityType> = Arrays.asList(
            MIXTER, SANTY, ELISA
    )
}
