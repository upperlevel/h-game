package xyz.upperlevel.hgame.world.character.impl

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import xyz.upperlevel.hgame.world.World
import xyz.upperlevel.hgame.world.character.EntityType
import xyz.upperlevel.hgame.world.character.Player
import xyz.upperlevel.hgame.world.character.SpriteExtractor
import xyz.upperlevel.hgame.world.sequence.Sequence

class Santy : EntityType {
    override val id = "santy"
    override val texturePath = "santy.png"

    override val width = 2f
    override val height = 2f

    override fun getSprites(texture: Texture): Array<Array<TextureRegion>> {
        return SpriteExtractor.grid(texture, 9, 4)
    }

    override fun create(world: World): ActorImpl {
        return ActorImpl(this, world)
    }

    inner class ActorImpl(entityType: EntityType, world: World) : Player(entityType, world) {
        init {
            jumpForce *= 0.5f
        }

        override fun attack(): Sequence {
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
                    .delay(2000)
                    .act { setFrame(0, 0) }
        }
    }
}
