package xyz.upperlevel.hgame.world.character.impl

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.FixtureDef
import com.badlogic.gdx.physics.box2d.PolygonShape
import org.lwjgl.util.vector.Vector2f
import xyz.upperlevel.hgame.event.Listener
import xyz.upperlevel.hgame.world.World
import com.badlogic.gdx.physics.box2d.World as PhysicsWorld
import xyz.upperlevel.hgame.world.character.Entity
import xyz.upperlevel.hgame.world.character.EntityType
import xyz.upperlevel.hgame.world.character.Player
import xyz.upperlevel.hgame.world.character.SpriteExtractor


class Mikrotik : EntityType, Listener {
    override val name = "Mikrotik"
    override val texturePath = "mikrotik.png"

    override fun getSprites(texture: Texture): Array<Array<TextureRegion>> {
        return SpriteExtractor.grid(texture, 1, 1)
    }

    private fun createBody(world: PhysicsWorld): Body {
        val bodyDef = BodyDef().apply {
            fixedRotation = true
            type = BodyDef.BodyType.DynamicBody
        }

        val bodyFixtureDef = FixtureDef().apply {
            shape =  PolygonShape().apply {
                val w = WIDTH / 2f
                val h = HEIGHT / 2f
                setAsBox(w, h, Vector2(w, h), 0f)
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

    override fun personify(id: Int, world: World): Entity {
        return Entity(id, world, createBody(world.physics), Vector2f(WIDTH, HEIGHT), this)
    }

    companion object {
        const val WIDTH  = .25f
        const val HEIGHT = .25f
    }
}
