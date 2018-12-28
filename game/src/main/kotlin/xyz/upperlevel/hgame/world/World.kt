package xyz.upperlevel.hgame.world

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.g2d.ParticleEffect
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.FixtureDef
import com.badlogic.gdx.physics.box2d.PolygonShape
import org.apache.logging.log4j.LogManager
import xyz.upperlevel.hgame.event.EventChannel
import xyz.upperlevel.hgame.network.Endpoint
import xyz.upperlevel.hgame.network.NetSide
import xyz.upperlevel.hgame.world.entity.*
import xyz.upperlevel.hgame.world.player.Player
import com.badlogic.gdx.physics.box2d.World as PhysicsWorld

class World {
    var height = 15.0f
    var groundHeight = 1.0f

    val physics = PhysicsWorld(Vector2(0f, -9.8f), true)
    var physicsAccumulator = 0f

    val events = EventChannel()

    private val entityRegistry = EntityRegistry(this)

    private val _effects: MutableList<ParticleEffect> = ArrayList()
    val effects: List<ParticleEffect>
        get() = _effects

    private var nextPopupId = 0
    private val _popups: MutableMap<Int, Popup> = HashMap()
    val popups: Collection<Popup>
        get() = ArrayList(_popups.values)

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

    fun spawnPlayer(name: String, type: EntityType) {
        var x = 20 / 4
        if (isMaster) x += 20 / 2

        val entity = type.create(this) as Player
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

    fun showPopup(text: String, x: Float, y: Float): Int {
        val id = nextPopupId++
        _popups[id] = Popup(this, id, text, x, y)
        return id
    }

    fun hidePopup(id: Int) {
        _popups.remove(id)
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

    fun update() {
        if (!isReady) return
        // Updates
        doPhysicsStep(Gdx.graphics.deltaTime)

        // TODO: Just to debug!!!
        if (Gdx.input.isKeyJustPressed(Input.Keys.H)) {
            player!!.damage(0.5f)
        }

        entityRegistry.entities.forEach { e -> e.update() }

        popups.forEach { popup -> popup.update() }

        _effects.removeIf { effect -> effect.isComplete }
    }

    fun despawn(entity: Entity) {
        entityRegistry.despawn(entity)
    }

    fun initEndpoint(endpoint: Endpoint) {
        this.endpoint = endpoint
        isMaster = endpoint.side == NetSide.MASTER

        entityRegistry.initEndpoint(endpoint)
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
