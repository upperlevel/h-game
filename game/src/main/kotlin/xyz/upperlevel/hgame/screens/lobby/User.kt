package xyz.upperlevel.hgame.screens.lobby

import xyz.upperlevel.hgame.world.entity.EntityType
import xyz.upperlevel.hgame.world.entity.EntityTypes

open class User(val name: String, open var entityType: EntityType = EntityTypes.SANTY) {
    open var ready: Boolean = false

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
