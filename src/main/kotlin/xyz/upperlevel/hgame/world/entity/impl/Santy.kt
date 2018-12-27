package xyz.upperlevel.hgame.world.entity.impl

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import org.apache.logging.log4j.LogManager
import xyz.upperlevel.hgame.world.World
import xyz.upperlevel.hgame.world.entity.*
import xyz.upperlevel.hgame.world.sequence.Sequence

class Santy : EntityType {
    override val id = "santy"
    override val texturePath = "santy.png"

    override val width = 2f
    override val height = 2f

    override fun getSprites(texture: Texture): Array<Array<TextureRegion>> {
        return SpriteExtractor.grid(texture, 9, 4)
    }

    override fun create(world: World, active: Boolean): ActorImpl {
        return ActorImpl(this, world, active)
    }

    inner class ActorImpl(entityType: EntityType, world: World, active: Boolean) : Player(entityType, world, active) {
        val attack = CloseRangeAttack(this)

        init {
            jumpForce *= 0.5f
            attack.setupFixtures()
        }

        override fun attack(): Sequence {
            attack.contact?.onAttacked(this)
            return Sequence.create()
                    .act { setFrame(0, 2) }
                    .delay(200)
                    .act { setFrame(1, 2) }
                    .delay(200)
                    .act { setFrame(0, 0) }
        }

        override fun specialAttack(): Sequence {
            return Sequence.create()
                    .repeat({ _, time -> setFrame(time % 2, 3) }, 200, 15)
                    .repeat({ _, time -> setFrame(time + 2, 3) }, 500, 2)
                    .repeat({ _, time -> setFrame(time + 4, 3) }, 200, 5)
                    .act {
                        val entity = EntityTypes.POISON.create(world)
                        entity.x = x + POISON_THROW_DISTANCE * (if (left) -1f else 1f)
                        entity.y = y - entity.height
                        entity.updatePos()
                        world.spawn(entity)
                    }
                    .delay(500)
                    .act { setFrame(0, 0) }
        }
    }

    companion object {
        private val logger = LogManager.getLogger()

        const val POISON_THROW_DISTANCE = 2.25f
    }
}
