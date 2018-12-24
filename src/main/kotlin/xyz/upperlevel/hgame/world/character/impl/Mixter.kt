package xyz.upperlevel.hgame.world.character.impl

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import org.apache.logging.log4j.LogManager
import xyz.upperlevel.hgame.world.World
import xyz.upperlevel.hgame.world.character.*
import xyz.upperlevel.hgame.world.character.CloseRangeAttack
import xyz.upperlevel.hgame.world.sequence.Sequence

class Mixter : EntityType {
    override val id = "mixter"
    override val texturePath = "mixter.png"

    override val width = 2f
    override val height = 2f

    override fun getSprites(texture: Texture): Array<Array<TextureRegion>> {
        return SpriteExtractor.grid(texture, 3, 4)
    }

    override fun create(world: World): Player {
        return ActorImpl(this, world)
    }

    private inner class ActorImpl(entityType: EntityType, world: World) : Player(entityType, world) {
        val attack = CloseRangeAttack(this)

        init {
            jumpForce *= 0.5f
            attack.setupFixtures()
        }

        override fun attack(): Sequence {

            return Sequence.create()
                    .act { setFrame(0, 2) }
                    .delay(100)
                    .act { setFrame(1, 2) }
                    .delay(250)
                    .act {
                        attack.contact?.onAttacked(this)
                        setFrame(2, 2)
                    }
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

                        // Only if the behaviour system is active (meaning that is used by the current host),
                        // we spawn the Mikrotik.
                        if (behaviour!!.active) {
                            val mikrotik = EntityTypes.MIKROTIK.create(world) as ThrowableEntity
                            this.chuck(mikrotik, power, Math.toRadians(45.0).toFloat())
                        }

                        setFrame(2, 3)
                    }
                    .delay(1000)
        }
    }

    companion object {
        private val logger = LogManager.getLogger()
    }
}
