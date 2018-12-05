package xyz.upperlevel.hgame.world.character.impl

import com.badlogic.gdx.physics.box2d.World
import xyz.upperlevel.hgame.world.character.Entity
import xyz.upperlevel.hgame.world.character.Character
import xyz.upperlevel.hgame.world.character.Player
import xyz.upperlevel.hgame.world.sequence.Sequence

class Santy : Character {
    override val name = "Santy"
    override val texturePath = "prof_santy.png"

    override fun personify(id: Int, pworld: World): ActorImpl {
        return ActorImpl(id, pworld, this)
    }

    inner class ActorImpl(id: Int, world: World, character: Character) : Player(id, world, character) {
        private val attackTask = -1

        // Special attack
        private val shakingTask = -1
        private val specialAttackTask = -1

        override fun move(offsetX: Float) {
            super.move(offsetX * 0.5f)
        }

        override fun jump(strength: Float) {
            super.jump(strength * 0.5f)
        }

        override fun attack() {
            animate(
                    Sequence.create()
                            .act { setFrame(0, 2) }
                            .delay(200)
                            .act { setFrame(1, 2) }
                            .delay(200)
                            .act { setFrame(0, 0) }
            )
        }

        override fun specialAttack() {
            animate(
                    Sequence.create()
                            .repeat({ _, time -> setFrame(time % 2, 3) }, 200, 15)
                            .repeat({ _, time -> setFrame(time + 2, 3) }, 500, 2)
                            .repeat({ _, time -> setFrame(time + 4, 3) }, 200, 5)
                            .delay(2000)
                            .act { setFrame(0, 0) }
            )
        }
    }
}
