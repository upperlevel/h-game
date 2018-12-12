package xyz.upperlevel.hgame.input

import xyz.upperlevel.hgame.world.character.Entity

class JumpBehaviour(behaviourMap: BehaviourMap, entity: Entity) : Behaviour(behaviourMap, "jump", entity) {
    override fun initialize() {
        hook({ entity.isTouchingGround }, behaviourMap["idle"]!!)
    }

    override fun onEnable() {
        entity.jump(1.0f)
    }
}
