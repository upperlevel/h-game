package xyz.upperlevel.hgame.world.character.impl

import org.apache.logging.log4j.LogManager
import xyz.upperlevel.hgame.world.character.Entity
import xyz.upperlevel.hgame.world.character.Character

class Sfera : Character {
    override val name = "Sfera"

    override val texturePath = "prof_sfera.png"

    override fun personify(id: Int): Entity {
        return ActorImpl(id, this)
    }

    private inner class ActorImpl(id: Int, character: Character) : Entity(id, character) {

        override fun move(offsetX: Float) {
            super.move(offsetX * 0.5f)
        }

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
