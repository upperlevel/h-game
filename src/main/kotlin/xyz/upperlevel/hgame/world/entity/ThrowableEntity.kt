package xyz.upperlevel.hgame.world.entity

import org.apache.logging.log4j.LogManager
import xyz.upperlevel.hgame.world.World


class ThrowableEntity(entityType: EntityType, world: World, active: Boolean) : Entity(entityType, world, active) {
    var thrower: Entity? = null

    override fun serialize(): EntitySpawnPacket {
        return ThrowableEntitySpawnPacket(entityType.id, id, x, y, left, if (thrower != null) thrower!!.id else -1)
    }

    override fun deserialize(packet: EntitySpawnPacket) {
        if (packet !is ThrowableEntitySpawnPacket) throw IllegalArgumentException("Given packet is not a ThrowableEntityPacket")
        super.deserialize(packet)
        if (packet.throwerEntityId >= 0) {
            thrower = world.getEntity(packet.throwerEntityId)!! // Must be found, otherwise it's a networking error.
        } else {
            thrower = null
            logger.warn("ThrowableEntity without a thrower.")
        }
    }

    companion object {
        private val logger = LogManager.getLogger()
    }
}

