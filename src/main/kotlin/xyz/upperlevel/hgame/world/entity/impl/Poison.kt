package xyz.upperlevel.hgame.world.entity.impl

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.FixtureDef
import com.badlogic.gdx.physics.box2d.PolygonShape
import xyz.upperlevel.hgame.world.World
import xyz.upperlevel.hgame.world.entity.EntityType
import xyz.upperlevel.hgame.world.entity.SpriteExtractor
import com.badlogic.gdx.physics.box2d.World as PhysicsWorld

class Poison : EntityType() {
    override val id = "poison"
    override val texturePath = "poison.png"

    override val width = 37f / 10f
    override val height = 5f / 10f

    override fun createSprites(texture: Texture): Array<Array<TextureRegion>> {
        return SpriteExtractor.grid(texture, 1, 1)
    }

    override fun create(world: World, active: Boolean): PoisonEntity {
        return PoisonEntity(world, active)
    }

    override fun createBody(world: PhysicsWorld): Body {
        val bodyDef = BodyDef().apply {
            type = BodyDef.BodyType.StaticBody
        }

        val bodyFixtureDef = FixtureDef().apply {
            shape = PolygonShape().apply {
                val w = width / 2f
                val h = height / 2f
                setAsBox(w, h, Vector2(0f, h), 0f)
            }
            isSensor = true
            density = 1f
        }
        val body = world.createBody(bodyDef)
        body.createFixture(bodyFixtureDef)
        return body
    }

}