package xyz.upperlevel.hgame.world

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.ParticleEffect
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.FixtureDef
import com.badlogic.gdx.physics.box2d.PolygonShape
import org.apache.logging.log4j.LogManager
import xyz.upperlevel.hgame.event.EventChannel
import xyz.upperlevel.hgame.event.EventListener
import xyz.upperlevel.hgame.network.Endpoint
import xyz.upperlevel.hgame.network.NetSide
import xyz.upperlevel.hgame.network.events.ConnectionOpenEvent
import xyz.upperlevel.hgame.runSync
import xyz.upperlevel.hgame.world.entity.Entity
import xyz.upperlevel.hgame.world.entity.EntityTypes
import xyz.upperlevel.hgame.world.entity.ThrowableEntity
import xyz.upperlevel.hgame.world.entity.EntityRegistry
import xyz.upperlevel.hgame.world.entity.impl.MikrotikEntity
import xyz.upperlevel.hgame.world.events.PhysicContactBeginEvent
import com.badlogic.gdx.physics.box2d.World as PhysicsWorld

class World {
    var height = 0.0f
    var groundHeight = 0.0f

    val physics = PhysicsWorld(Vector2(0f, -9.8f), true)
    var physicsAccumulator = 0f

    val events = EventChannel()

    private val entityRegistry = EntityRegistry(this)

    private val _effects: MutableList<ParticleEffect> = ArrayList()
    val effects: List<ParticleEffect>
        get() = _effects

    var player: Entity? = null
        private set

    private var isMaster = false

    val entities: Collection<Entity>
        get() = entityRegistry.entities

    // TODO: isn't there a cleaner way to do this? like waiting in another screen
    val isReady: Boolean
        get() = player != null

    lateinit var endpoint: Endpoint

    init {
        height = 15.0f
        groundHeight = 1.0f
        physics.setContactListener(EventContactListener(events))

        events.register(EntityGroundListener()) // Listen for ground contacts
        events.register(FixtureSensorCaller())

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

    fun onGameStart(endpoint: Endpoint, name: String) {
        var x = 20 / 4
        if (isMaster) x += 20 / 2

        val entity = EntityTypes.MIXTER.create(this)
        entity.setPosition(x.toFloat(), 0f)
        entity.left = isMaster
        entity.name = name
        spawn(entity)
        player = entity

        entity.behaviour?.let { it.endpoint = endpoint }
    }

    fun getEntity(entityId: Int): Entity? {
        return entityRegistry.getEntity(entityId)
    }

    fun spawn(entity: Entity) {
        entityRegistry.spawn(entity)
    }

    fun doEffect(effect: ParticleEffect, x: Float, y: Float) {
        _effects.add(effect)

        effect.setPosition(x, y)

        effect.start()
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

        entityRegistry.clearDestroyedBodies()
    }

    fun update(endpoint: Endpoint) { // TODO endpoint here?
        if (!isReady) return
        // Updates
        doPhysicsStep(Gdx.graphics.deltaTime)
        entityRegistry.entities.forEach { e -> e.update() }

        _effects.removeIf { effect -> effect.isComplete }
    }

    fun despawn(entity: Entity) {
        entityRegistry.despawn(entity)
    }

    fun initEndpoint(endpoint: Endpoint, username: String) {
        this.endpoint = endpoint
        isMaster = endpoint.side == NetSide.MASTER

        entityRegistry.initEndpoint(endpoint)

        endpoint.events.register(EventListener.listener(ConnectionOpenEvent::class.java, {
            runSync { this.onGameStart(endpoint, username) }
        }))
    }

    companion object {
        // Physics constants
        const val TIME_STEP = 1f / 60
        const val VELOCITY_ITERATIONS = 6
        const val POSITION_ITERATIONS = 2

        val GROUND_DATA = Object()

        private val logger = LogManager.getLogger()
    }
}
