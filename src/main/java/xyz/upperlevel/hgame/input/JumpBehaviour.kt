package xyz.upperlevel.hgame.input

import xyz.upperlevel.hgame.world.character.Actor

class JumpBehaviour(behaviourMap: BehaviourMap, actor: Actor) : Behaviour(behaviourMap, "jump", actor) {
    override fun initialize() {
        // TODO: touchGround -> idle
    }

    override fun onEnable(): Behaviour? {
        // TODO: apply impulse
        return null
    }
}
