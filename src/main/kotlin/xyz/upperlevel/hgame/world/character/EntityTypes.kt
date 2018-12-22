package xyz.upperlevel.hgame.world.character

import xyz.upperlevel.hgame.world.character.impl.Mikrotik
import xyz.upperlevel.hgame.world.character.impl.Mixter
import xyz.upperlevel.hgame.world.character.impl.Santy


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
