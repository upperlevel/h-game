package xyz.upperlevel.hgame.input

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import xyz.upperlevel.hgame.world.character.Actor

class WalkRightBehaviour(behaviourMap: BehaviourMap, actor: Actor) : Behaviour(behaviourMap, "walk_right", actor) {
    override fun initialize() {
        hook({ !Gdx.input.isKeyPressed(Input.Keys.D) }, behaviourMap["idle"]!!)
        Behaviour.addDefault(this)
    }

    override fun onEnable(): Behaviour? {
        // TODO: Apply force
        return super.onEnable()
    }

    override fun onDisable() {
        super.onDisable()
        // TODO: Disable force
    }
}
