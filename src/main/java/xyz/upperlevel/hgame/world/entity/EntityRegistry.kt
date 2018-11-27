package xyz.upperlevel.hgame.world.entity

import xyz.upperlevel.hgame.input.TriggerInputActionPacket
import xyz.upperlevel.hgame.network.Endpoint
import xyz.upperlevel.hgame.network.NetSide
import xyz.upperlevel.hgame.runSync
import xyz.upperlevel.hgame.world.character.Actor
import xyz.upperlevel.hgame.world.character.Character
import java.util.*
import java.util.stream.Stream

typealias Callback = (Actor) -> Unit

class EntityRegistry {
    // TODO: use actors instead of entities
    private val factories = ArrayList<EntityFactory>()
    private val factoryIdByType = HashMap<Class<*>, Int>()
    private val pendingSpawns = ArrayDeque<Callback>()

    private val _entities = HashMap<Int, Actor>()

    val entities: Stream<Actor>
        get() = _entities.values.stream()

    private var nextId = 0

    private var endpoint: Endpoint? = null

    private fun spawn0(type: Int, x: Float, y: Float, left: Boolean): Actor {
        val factory = factories[type]

        val entity = factory(nextId++)
        entity.x = x
        entity.y = y
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

        endpoint.events.register(EntitySpawnPacket::class.java) { packet -> runSync { onNetSpawn(packet.entityTypeId, packet.x, packet.y, packet.isFacingLeft, packet.isConfirmation) } }
        endpoint.events.register(TriggerInputActionPacket::class.java) { (actorId, actionId) ->
            runSync {
                _entities[actorId]!!
                        .input
                        .onNetworkAction(actionId)
            }
        }
    }
}
