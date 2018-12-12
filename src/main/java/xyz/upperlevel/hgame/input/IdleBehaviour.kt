package xyz.upperlevel.hgame.input

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import xyz.upperlevel.hgame.world.character.Actor

class IdleBehaviour(behaviourMap: BehaviourMap, actor: Actor) : Behaviour(behaviourMap, "idle", actor) {
    override fun initialize() {
        hook({ Gdx.input.isKeyPressed(Input.Keys.A) }, behaviourMap["walk_left"]!!)
        hook({ Gdx.input.isKeyPressed(Input.Keys.D) }, behaviourMap["walk_right"]!!)
        Behaviour.addDefault(this)
    }
}
