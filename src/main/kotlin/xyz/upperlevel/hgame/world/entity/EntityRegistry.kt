package xyz.upperlevel.hgame.world.entity

import com.badlogic.gdx.physics.box2d.Body
import org.apache.logging.log4j.LogManager
import xyz.upperlevel.hgame.input.BehaviourChangePacket
import xyz.upperlevel.hgame.network.Endpoint
import xyz.upperlevel.hgame.network.NetSide
import xyz.upperlevel.hgame.runSync
import xyz.upperlevel.hgame.world.World
import xyz.upperlevel.hgame.world.player.Player
import xyz.upperlevel.hgame.world.player.PlayerJumpPacket
import xyz.upperlevel.hgame.world.sequence.Sequence

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

    init {
        Sequence.create()
                .repeat({ sendReset() }, 1000)
                .play()
    }

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

    private fun createResetPacket(entity: Entity): EntityResetPacket {
        val data = HashMap<String, Any>()
        entity.fillResetPacket(data)
        return EntityResetPacket(entity.id, data)
    }

    fun sendReset() {
        logger.debug("sending reset")
        endpoint?.let { endp ->
            _entities.map { it.value }
                    .filter { it.active }
                    .map { createResetPacket(it) }
                    .forEach { endp.send(it) }
        }
    }

    fun initEndpoint(endpoint: Endpoint) {
        this.endpoint = endpoint

        endpoint.events.register(EntitySpawnPacket::class.java, { packet ->
            runSync {
                logger.info("Received entity ${packet.entityTypeId} and id=${packet.entityId}")

                val entity = EntityTypes[packet.entityTypeId]!!.create(world, false)
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
        endpoint.events.register(EntityImpulsePacket::class.java, { packet ->
            runSync {
                _entities[packet.entityId]!!.impulse(packet.powerX, packet.powerY, packet.pointX, packet.pointY, false)
            }
        })
        endpoint.events.register(EntityResetPacket::class.java, {packet ->
            runSync {
                logger.debug("received reset {}", packet.entityId)
                val entity = _entities[packet.entityId]
                if (entity == null) {
                    logger.warn("Invalid reset packet: no entity with id ${packet.entityId}")
                    return@runSync
                }
                entity.onReset(packet.data)
            }
        })
    }

    companion object {
        private val logger = LogManager.getLogger()
    }
}
