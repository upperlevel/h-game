package xyz.upperlevel.hgame.world.character

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import org.lwjgl.util.vector.Vector2f
import xyz.upperlevel.hgame.input.BehaviourMap

open class Player(id: Int, world: World, character: Character)
        : Entity(
                id,
                createBody(world),
                Vector2f(WIDTH, HEIGHT),
                character) {

    init {
        // Creates a default BehaviourMap for the Player.
        // Each Player should have one.
        behaviourMap = BehaviourMap.createDefault(this)
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

            bodyFixtureDef = FixtureDef().apply {
                shape =  PolygonShape().apply {
                    val w = WIDTH / 2f
                    val h = HEIGHT / 2f
                    setAsBox(w, h, Vector2(w, h), 0f)
                }
                density = 1f
            }
        }

        private fun createBody(world: World): Body {
            val body = world.createBody(bodyDef)
            body.createFixture(bodyFixtureDef)
            return body
        }
    }
}