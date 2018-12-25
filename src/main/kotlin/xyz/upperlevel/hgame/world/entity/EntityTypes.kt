package xyz.upperlevel.hgame.world.entity

import xyz.upperlevel.hgame.world.entity.impl.Mikrotik
import xyz.upperlevel.hgame.world.entity.impl.Mixter
import xyz.upperlevel.hgame.world.entity.impl.Santy


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
}
