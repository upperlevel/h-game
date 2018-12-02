package xyz.upperlevel.hgame.world.character

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.physics.box2d.*
import org.lwjgl.util.vector.Vector2f

class Player(id: Int, world: World, character: Character) :
        Entity(
                id,
                createBody(world),
                Vector2f(WIDTH, HEIGHT),
                character) {


    init {
        val texture = Texture(Gdx.files.internal("images/" + character.texturePath))

        sprite = Sprite(texture)
        sprite.setSize(WIDTH, HEIGHT)

        regions = SpriteExtractor.grid(texture, 9, 4)
    }

    private fun createSensor(): Fixture {
        var fdef = FixtureDef()
        fdef.isSensor = true
        var shape = PolygonShape()
        shape.setAsBox()
    }

    init {

    }

    companion object {
        const val WIDTH = 2.0f
        const val HEIGHT = 2.0f

        val bodyDef: BodyDef
        val bodyFixtureDef: FixtureDef

        init {
            bodyDef = BodyDef().apply {
                fixedRotation = true
                type = BodyDef.BodyType.DynamicBody
            }

            val bodyShapeDef = PolygonShape().apply {
                setAsBox(WIDTH, HEIGHT)
            }

            bodyFixtureDef = FixtureDef().apply {
                shape = bodyShapeDef
                density = 1f
            }
        }

        private fun createBody(world: World): Body {
            val body = world.createBody(bodyDef)
            body.createFixture(bodyFixtureDef)
        }
    }
}