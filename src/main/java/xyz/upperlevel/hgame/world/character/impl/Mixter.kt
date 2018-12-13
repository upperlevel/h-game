package xyz.upperlevel.hgame.world.character.impl

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.physics.box2d.World
import xyz.upperlevel.hgame.world.character.Character
import xyz.upperlevel.hgame.world.character.Entity
import xyz.upperlevel.hgame.world.character.Player
import xyz.upperlevel.hgame.world.character.SpriteExtractor
import xyz.upperlevel.hgame.world.sequence.Sequence

class Mixter : Character {
    override val name = "Mixter"
    override val texturePath = "mixter.png"

    override fun getSprites(texture: Texture): Array<Array<TextureRegion>> {
        return SpriteExtractor.grid(texture, 3, 4)
    }

    override fun personify(id: Int, pworld: World): Entity {
        return ActorImpl(id, pworld, this)
    }

    private inner class ActorImpl(id: Int, world: World, character: Character) : Player(id, world, character) {
        override fun jump(velocity: Float) {
            super.jump(velocity * 0.5f)
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
                        // TODO spawn Mikrotik here
                        setFrame(2, 3)
                    }
                    .delay(1000)
        }
    }
}
