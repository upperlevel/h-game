package xyz.upperlevel.hgame.world.entity

import com.badlogic.gdx.physics.box2d.Body
import org.apache.logging.log4j.LogManager
import xyz.upperlevel.hgame.input.BehaviourChangePacket
import xyz.upperlevel.hgame.network.Endpoint
import xyz.upperlevel.hgame.network.NetSide
import xyz.upperlevel.hgame.runSync
import xyz.upperlevel.hgame.world.World
import xyz.upperlevel.hgame.world.character.Entity
import xyz.upperlevel.hgame.world.character.EntityTypes
import xyz.upperlevel.hgame.world.character.Player
import xyz.upperlevel.hgame.world.character.PlayerJumpPacket
import java.util.*
import kotlin.collections.ArrayList

class EntityRegistry(val world: World) {
    private val _entities = HashMap<Int, Entity>()

    val entities: Collection<Entity>
        get() = _entities.values

    private val playerCount = 2
    private var localId = 0
    private var localOffset = -1

    private var endpoint: Endpoint? = null
        set(value) {
            localOffset = when (value!!.side) {
                NetSide.MASTER -> 0
                NetSide.SLAVE -> 1
            }
            field = value
        }

    /**
     * List of bodies that are waiting to be removed from this world.
     * We have to do that because we can only destroy the body after physics world step.
     */
    private val pendingDestruction: MutableList<Body> = ArrayList()

    fun getEntity(entityId: Int): Entity? {
        return _entities[entityId]
    }

    private fun forceSpawn(entity: Entity) {
        // Assigns entity id if not present.
        if (entity.id < 0) entity.id = localId++ * playerCount + localOffset

        // Spawns the entity.
        _entities[entity.id] = entity

        world.events.call(EntitySpawnEvent(entity))

        logger.info("Created entity type=${entity.entityType.id} with id=${entity.id} (side=${endpoint!!.side.name})")
    }

    fun spawn(entity: Entity) {
        if (!entity.spawned) {
            forceSpawn(entity)
            endpoint!!.send(entity.serialize())
        }
    }

    private fun strictDespawn(entity: Entity) {
        if (entity.spawned) {
            _entities.remove(entity.id)
            entity.id = -1
            pendingDestruction.add(entity.body)
        }
    }

    fun despawn(entity: Entity) {
        if (entity.spawned) {
            strictDespawn(entity)
            // Currently no despawn packet, the logic is the same so both endpoint should despawn at the same time.
        }
    }

    fun clearDestroyedBodies() {
        if (pendingDestruction.size > 0) {
            logger.info("${pendingDestruction.size} entities pending destruction will be removed.")
        }
        pendingDestruction.forEach { body ->
            world.physics.destroyBody(body)
        }
        pendingDestruction.clear()
    }

    fun initEndpoint(endpoint: Endpoint) {
        this.endpoint = endpoint

        endpoint.events.register(EntitySpawnPacket::class.java, { packet ->
            runSync {
                logger.info("Received entity ${packet.entityTypeId} and id=${packet.entityId}")

                val entity = EntityTypes[packet.entityTypeId]!!.create(world)
                entity.deserialize(packet)
                forceSpawn(entity)
            }
        })
        endpoint.events.register(BehaviourChangePacket::class.java, { packet ->
            runSync {
                val res = _entities[packet.actorId]
                        ?.behaviour
                        ?.layers
                        ?.get(packet.layerIndex)
                        ?.active(packet.behaviour)
                if (res == null) {
                    logger.warn("Invalid packet")
                }
            }
        })
        endpoint.events.register(PlayerJumpPacket::class.java, { packet ->
            runSync {
                val player = _entities[packet.entityId]
                if (player == null || player !is Player) {
                    logger.warn("Invalid packet: invalid entity")
                    return@runSync
                }// TODO: log error
                player.jump()
            }
        })
    }

    companion object {
        private val logger = LogManager.getLogger()
    }
}
