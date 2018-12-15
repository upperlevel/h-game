package xyz.upperlevel.hgame.world.character.impl

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.physics.box2d.World
import xyz.upperlevel.hgame.world.character.Character
import xyz.upperlevel.hgame.world.character.Player
import xyz.upperlevel.hgame.world.character.SpriteExtractor
import xyz.upperlevel.hgame.world.sequence.Sequence

class Santy : Character {
    override val name = "Santy"
    override val texturePath = "santy.png"

    override fun getSprites(texture: Texture): Array<Array<TextureRegion>> {
        return SpriteExtractor.grid(texture, 9, 4)
    }

    override fun personify(id: Int, pworld: World): ActorImpl {
        return ActorImpl(id, pworld, this)
    }

    inner class ActorImpl(id: Int, world: World, character: Character) : Player(id, world, character) {
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
