package xyz.upperlevel.hgame.world

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.FixtureDef
import com.badlogic.gdx.physics.box2d.PolygonShape
import xyz.upperlevel.hgame.event.EventChannel
import xyz.upperlevel.hgame.event.EventListener
import xyz.upperlevel.hgame.network.Endpoint
import xyz.upperlevel.hgame.network.NetSide
import xyz.upperlevel.hgame.network.events.ConnectionOpenEvent
import xyz.upperlevel.hgame.runSync
import xyz.upperlevel.hgame.world.character.Entity
import xyz.upperlevel.hgame.world.character.Player
import xyz.upperlevel.hgame.world.character.impl.Mixter
import xyz.upperlevel.hgame.world.character.impl.Santy
import xyz.upperlevel.hgame.world.entity.EntityRegistry
import java.util.stream.Stream
import com.badlogic.gdx.physics.box2d.World as PhysicsWorld

class World {
    var height = 0.0f
    var groundHeight = 0.0f

    val physics = PhysicsWorld(Vector2(0f, -9.8f), true)
    var physicsAccumulator = 0f

    private val entityRegistry = EntityRegistry()

    var player: Entity? = null
        private set

    private var isMaster = false

    val events = EventChannel()

    val entities: Stream<Entity>
        get() = entityRegistry.entities

    // TODO: isn't there a cleaner way to do this? like waiting in another screen
    val isReady: Boolean
        get() = player != null

    init {
        height = 5.0f
        groundHeight = 1.0f
        physics.setContactListener(EventContactListener(events))
        events.register(EntityGroundListener()) // Listen for ground contacts

        // Spawn the ground
        val groundWidth = 20f
        val groundHeight = 10f
        val ground = physics.createBody(BodyDef().apply {
            type = BodyDef.BodyType.StaticBody
        })
        ground.createFixture(FixtureDef().apply {
            density = 1f
            shape = PolygonShape().apply {
                // Create a w*h box centered at the top middle
                val w = groundWidth / 2f
                val h = groundHeight / 2f
                setAsBox(w, h, Vector2(w, -h), 0f)
            }
        })
        ground.userData = GROUND_DATA// Used in ground touch listener
    }

    fun onGameStart(endpoint: Endpoint) {
        var x = 20 / 4
        if (isMaster) x += 20 / 2
        entityRegistry.spawn(Mixter::class.java, x.toFloat(), 0f, isMaster) { spawned ->
            player = spawned
            (spawned as Player).active = true
        }
    }

    private fun doPhysicsStep(deltaTime: Float) {
        // fixed time step

        // TODO: we should find a way to avoid spiral of death without limiting the time frame (yeah, networking)
        // max frame time to avoid spiral of death (on slow devices) DISABLED
        // val frameTime = Math.min(deltaTime, 0.25f) // Remove comment to enable

        physicsAccumulator += deltaTime
        while (physicsAccumulator >= TIME_STEP) {
            entityRegistry.entities.forEach { it.prePhysicStep(this) }
            physics.step(TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS)
            physicsAccumulator -= TIME_STEP
        }
    }

    fun update(endpoint: Endpoint) { // TODO endpoint here?
        if (!isReady) return
        // Updates
        doPhysicsStep(Gdx.graphics.deltaTime)
        entityRegistry.entities.forEach { e -> e.update(this) }
    }

    fun initEndpoint(endpoint: Endpoint) {
        isMaster = endpoint.side == NetSide.MASTER

        // TODO: better type management
        entityRegistry.registerType(Santy::class.java) { Santy().personify(it, physics) }
        entityRegistry.registerType(Mixter::class.java) { Mixter().personify(it, physics) }
        entityRegistry.initEndpoint(endpoint)

        endpoint.events.register(EventListener.listener(ConnectionOpenEvent::class.java, {
            runSync { this.onGameStart(endpoint) }
        }))
    }

    companion object {
        // Physics constants
        const val TIME_STEP = 1f/60
        const val VELOCITY_ITERATIONS = 6
        const val POSITION_ITERATIONS = 2

        val GROUND_DATA = Object()
    }
}
