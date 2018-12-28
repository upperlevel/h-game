package xyz.upperlevel.hgame.world.entity

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.FixtureDef
import com.badlogic.gdx.physics.box2d.PolygonShape
import xyz.upperlevel.hgame.world.World
import com.badlogic.gdx.physics.box2d.World as PhysicsWorld


abstract class EntityType {
    abstract val id: String
    abstract val texturePath: String

    abstract val width: Float
    abstract val height: Float

    val texture by lazy { Texture(Gdx.files.internal("images/$texturePath")) }

    val regions by lazy { createSprites(texture) }

    open fun createBody(world: PhysicsWorld): Body {
        val bodyDef = BodyDef().apply {
            fixedRotation = true
            type = BodyDef.BodyType.DynamicBody
        }

        val bodyFixtureDef = FixtureDef().apply {
            shape = PolygonShape().apply {
                val w = width / 2f
                val h = height / 2f
                setAsBox(w, h, Vector2(0f, h), 0f)
            }
            density = 1f
            filter.apply {
                categoryBits = 0x2 // Category 0x2 (default is 0x1)
                maskBits = 0x2.inv()// Collides with everything but 0x2
            }
        }
        val body = world.createBody(bodyDef)
        body.createFixture(bodyFixtureDef)
        return body
    }

    abstract fun createSprites(texture: Texture): Array<Array<TextureRegion>>

    abstract fun create(world: World, active: Boolean = true): Entity
}


