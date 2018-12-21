package xyz.upperlevel.hgame.world.character.impl

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import xyz.upperlevel.hgame.world.EntityGroundListener
import xyz.upperlevel.hgame.world.World
import xyz.upperlevel.hgame.world.character.Entity
import xyz.upperlevel.hgame.world.character.EntityType
import xyz.upperlevel.hgame.world.character.Player
import xyz.upperlevel.hgame.world.character.SpriteExtractor
import xyz.upperlevel.hgame.world.sequence.Sequence

class Mixter : EntityType {
    override val name = "Mixter"
    override val texturePath = "mixter.png"

    override fun getSprites(texture: Texture): Array<Array<TextureRegion>> {
        return SpriteExtractor.grid(texture, 3, 4)
    }

    override fun personify(id: Int, world: World): Entity {
        return ActorImpl(id, world, this)
    }

    private inner class ActorImpl(id: Int, world: World, entityType: EntityType) : Player(id, world, entityType) {
        init {
            jumpForce *= 0.5f
        }

        override fun attack(): Sequence {
            return Sequence.create()
                    .act { setFrame(0, 2) }
                    .delay(100)
                    .act { setFrame(1, 2) }
                    .delay(250)
                    .act { setFrame(2, 2) }
                    .delay(500)
        }

        override fun specialAttack(): Sequence {
            return Sequence.create()
                    .act { setFrame(0, 3) }
                    .delay(100)
                    .act { setFrame(1, 3) }
                    .delay(250)
                    .act {
                        val powerX = .5f
                        val powerY = .25f
                        val x = x + Player.WIDTH / 2f + if (left) -Mikrotik.WIDTH else 0f
                        val y = y + Player.HEIGHT / 2f

                        world.spawn(Mikrotik::class.java, x, y) { spawned ->
                            // Applies a force to the Mikrotik.
                            spawned.body.applyLinearImpulse(
                                    Vector2(if (left) -powerX else powerX, powerY),
                                    Vector2(spawned.x, spawned.y),
                                    true
                            )
                        }
                        setFrame(2, 3)
                    }
                    .delay(1000)
        }
    }
}
