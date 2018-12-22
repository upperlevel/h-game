package xyz.upperlevel.hgame.world.character.impl

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import org.apache.logging.log4j.LogManager
import xyz.upperlevel.hgame.world.World
import xyz.upperlevel.hgame.world.character.*
import xyz.upperlevel.hgame.world.sequence.Sequence

class Mixter : EntityType {
    override val id = "mixter"
    override val texturePath = "mixter.png"

    override val width = 2f
    override val height = 2f

    override fun getSprites(texture: Texture): Array<Array<TextureRegion>> {
        return SpriteExtractor.grid(texture, 3, 4)
    }

    override fun create(world: World): Entity {
        return ActorImpl(this, world)
    }

    private inner class ActorImpl(entityType: EntityType, world: World) : Player(entityType, world) {
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
                        val power = EntityTypes.MIKROTIK.width * 2f

                        // Creates the Mikrotik and makes Mixter chucks it (will spawn it).
                        val mikrotik = EntityTypes.MIKROTIK.create(world) as ThrowableEntity
                        this.chuck(mikrotik, power, Math.toRadians(45.0).toFloat())

                        setFrame(2, 3)
                    }
                    .delay(1000)
        }
    }

    companion object {
        private val logger = LogManager.getLogger()
    }
}
