package xyz.upperlevel.hgame.world.entity

import com.badlogic.gdx.physics.box2d.Body
import org.apache.logging.log4j.LogManager
import xyz.upperlevel.hgame.input.BehaviourChangePacket
import xyz.upperlevel.hgame.network.Endpoint
import xyz.upperlevel.hgame.runSync
import xyz.upperlevel.hgame.world.World
import xyz.upperlevel.hgame.world.character.Entity
import xyz.upperlevel.hgame.world.character.EntityTypes
import xyz.upperlevel.hgame.world.character.Player
import xyz.upperlevel.hgame.world.character.PlayerJumpPacket
import java.util.*
import kotlin.collections.ArrayList

typealias Callback = (Entity) -> Unit

class EntityRegistry(val world: World) {
    // TODO: use actors instead of entities
    private val pendingSpawns = ArrayDeque<Callback>()

    private val _entities = HashMap<Int, Entity>()

    val entities: Collection<Entity>
        get() = _entities.values

    private var nextId = 0

    private var endpoint: Endpoint? = null

    /**
     * List of bodies that are waiting to be removed from this world.
     * We have to do that because we can only destroy the body after physics world step.
     */
    private val pendingDestruction: MutableList<Body> = ArrayList()

    fun getEntity(entityId: Int): Entity? {
        return _entities[entityId]
    }

    private fun strictSpawn(entity: Entity) {
        if (!entity.spawned) {
            entity.id = nextId++
            _entities[entity.id] = entity

            world.events.call(EntitySpawnEvent(entity))
        }
    }

    fun spawn(entity: Entity) {
        if (!entity.spawned) {
            strictSpawn(entity)
            endpoint!!.send(entity.serialize())
        }
    }

    private fun strictDespawn(entity: Entity) {
        if (entity.spawned) {
            _entities.remove(entity.id)
            entity.id = -1
            pendingDestruction.add(entity.body)
            logger.info("Initiated despawn process for entity: ${entity.id}")
        }
    }

    fun despawn(entity: Entity) {
        if (entity.spawned) {
            strictDespawn(entity)
            endpoint!!.send(EntityDespawnPacket(entity.id))
            logger.info("Despawn packet sent for entity: ${entity.id}")
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
                var entity = getEntity(packet.entityId)

                // The entity wasn't found, we need to spawn it.
                if (entity == null) {
                    entity = EntityTypes[packet.entityTypeId]!!.create(world)
                    strictSpawn(entity)
                }
                entity.deserialize(packet)
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
        endpoint.events.register(EntityDespawnPacket::class.java, { packet ->
            runSync { strictDespawn(_entities[packet.entityId]!!) }
        })
    }

    companion object {
        private val logger = LogManager.getLogger()
    }
}
