package xyz.upperlevel.hgame.world

import xyz.upperlevel.hgame.event.EventListener
import xyz.upperlevel.hgame.network.Endpoint
import xyz.upperlevel.hgame.network.NetSide
import xyz.upperlevel.hgame.network.events.ConnectionOpenEvent
import xyz.upperlevel.hgame.runSync
import xyz.upperlevel.hgame.world.character.Entity
import xyz.upperlevel.hgame.world.character.controllers.RemoteController
import xyz.upperlevel.hgame.world.character.impl.Santy
import xyz.upperlevel.hgame.world.entity.EntityRegistry
import java.util.stream.Stream

class World {
    var height = 0.0f
    var gravity = 0.0f
    var groundHeight = 0.0f

    private val entityRegistry = EntityRegistry()

    var player: Entity? = null
        private set

    private var isMaster = false

    val entities: Stream<Entity>
        get() = entityRegistry.entities

    // TODO: isn't there a cleaner way to do this? like waiting in another screen
    val isReady: Boolean
        get() = player != null

    init {
        height = 5.0f
        gravity = 9.8f
        groundHeight = 1.0f
    }

    fun onGameStart(endpoint: Endpoint) {
        var x = 20 / 4
        if (isMaster) x += 20 / 2
        entityRegistry.spawn(Santy::class.java, x.toFloat(), groundHeight, isMaster) { spawned ->
            player = spawned
            RemoteController.bind(player!!, endpoint);
        }
    }

    fun update(endpoint: Endpoint) { // TODO endpoint here?
        if (!isReady) return
        // Updates
        entityRegistry.entities.forEach { e -> e.update(this) }
    }

    fun initEndpoint(endpoint: Endpoint) {
        isMaster = endpoint.side == NetSide.MASTER

        // TODO: better type management
        entityRegistry.registerType(Santy::class.java) { Santy().personify(it) }
        entityRegistry.initEndpoint(endpoint)

        endpoint.events.register(EventListener.listener(ConnectionOpenEvent::class.java) {
            runSync { this.onGameStart(endpoint) }
        })
    }

    companion object {
        const val ACTOR_MOVE_SPEED = 0.05f
        const val ACTOR_JUMP_SPEED = 2f
    }
}
