package xyz.upperlevel.hgame.world.character.impl

import com.badlogic.gdx.physics.box2d.World
import org.apache.logging.log4j.LogManager
import xyz.upperlevel.hgame.world.character.Entity
import xyz.upperlevel.hgame.world.character.Character
import xyz.upperlevel.hgame.world.character.Player

class Sfera : Character {
    override val name = "Sfera"

    override val texturePath = "prof_sfera.png"

    override fun personify(id: Int, pworld: World): Entity {
        return ActorImpl(id, pworld, this)
    }

    private inner class ActorImpl(id: Int, world: World, character: Character) : Player(id, world, character) {
        override fun jump(strength: Float) {
            super.jump(strength * 0.5f)
        }

        override fun specialAttack() {
            logger.warn("{}: my special attack!", name)
        }
    }

    companion object {
        private val logger = LogManager.getLogger()
    }
}
