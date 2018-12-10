package xyz.upperlevel.hgame.world.entity

import xyz.upperlevel.hgame.network.Endpoint
import xyz.upperlevel.hgame.network.NetSide
import xyz.upperlevel.hgame.runSync
import xyz.upperlevel.hgame.world.character.Entity
import xyz.upperlevel.hgame.world.character.Character
import java.util.*
import java.util.stream.Stream

typealias Callback = (Entity) -> Unit

class EntityRegistry {
    // TODO: use actors instead of entities
    private val factories = ArrayList<EntityFactory>()
    private val factoryIdByType = HashMap<Class<*>, Int>()
    private val pendingSpawns = ArrayDeque<Callback>()

    private val _entities = HashMap<Int, Entity>()

    val entities: Stream<Entity>
        get() = _entities.values.stream()

    private var nextId = 0

    private var endpoint: Endpoint? = null

    private fun spawn0(type: Int, x: Float, y: Float, left: Boolean): Entity {
        val factory = factories[type]

        val entity = factory(nextId++)
        entity.body.setTransform(x, y, 0f)
        entity.left = left
        _entities[entity.id] = entity
        return entity
    }

    fun spawn(entityType: Class<out Character>, x: Float, y: Float, left: Boolean, callback: Callback) {
        val id = factoryIdByType[entityType]
            ?: throw IllegalArgumentException("Entity $entityType not registered!")

        endpoint!!.send(EntitySpawnPacket(id, x, y, left, false))

        if (endpoint!!.side === NetSide.MASTER) {
            val actor = spawn0(id, x, y, left)
            callback(actor)
        } else {
            pendingSpawns.add(callback)
        }
    }

    private fun onNetSpawn(typeId: Int, x: Float, y: Float, facingLeft: Boolean, isConfirm: Boolean) {
        val entity = spawn0(typeId, x, y, facingLeft)

        if (endpoint!!.side === NetSide.MASTER) {
            endpoint!!.send(EntitySpawnPacket(typeId, x, y, facingLeft, true))
        } else if (isConfirm) {
            // We are the client and the server sent a confirmation to our request
            pendingSpawns.remove()(entity)
        }
    }

    fun registerType(type: Class<*>, actorFactory: EntityFactory) {
        factoryIdByType[type] = factories.size
        factories.add(actorFactory)
    }

    fun initEndpoint(endpoint: Endpoint) {
        this.endpoint = endpoint

        endpoint.events.register(EntitySpawnPacket::class.java, { packet: EntitySpawnPacket -> runSync { onNetSpawn(packet.entityTypeId, packet.x, packet.y, packet.isFacingLeft, packet.isConfirmation) } })
        endpoint.events.register(TriggerInputActionPacket::class.java, { (actorId, actionId) ->
            runSync {
                _entities[actorId]!!
                        .controller
                        .issue(actionId)
            }
        })
    }
}
