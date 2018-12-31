package xyz.upperlevel.hgame.screens.lobby

import xyz.upperlevel.hgame.world.entity.EntityType
import xyz.upperlevel.hgame.world.entity.EntityTypes

class User(val name: String, var entityType: EntityType = EntityTypes.SANTY) {
    var ready: Boolean = false

    override fun hashCode(): Int {
        return name.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other is User) {
            return name == other.name
        }
        return super.equals(other)
    }
}
