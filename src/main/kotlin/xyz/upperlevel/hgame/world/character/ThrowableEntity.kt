package xyz.upperlevel.hgame.world.character

import com.badlogic.gdx.math.Vector2
import org.apache.logging.log4j.LogManager
import xyz.upperlevel.hgame.world.World
import xyz.upperlevel.hgame.world.entity.EntitySpawnPacket
import xyz.upperlevel.hgame.world.entity.ThrowableEntitySpawnPacket


class ThrowableEntity(entityType: EntityType, world: World) : Entity(entityType, world) {
    var thrower: Entity? = null
        set(value) {
            field = value

            // TODO horrible, every time the thrower is set applies the force.
            val power  = width * 2f
            val powerX = (Math.cos(45.0) * power).toFloat()
            val powerY = (Math.sin(45.0) * power).toFloat()
            body.applyLinearImpulse(
                    Vector2(if (field!!.left) -powerX else powerX, powerY),
                    Vector2(x + width / 2f, y + height / 2f),
                    true
            )
        }

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

