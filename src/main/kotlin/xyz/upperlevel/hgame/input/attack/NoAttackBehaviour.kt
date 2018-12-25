package xyz.upperlevel.hgame.input.attack

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import xyz.upperlevel.hgame.input.Behaviour
import xyz.upperlevel.hgame.input.BehaviourLayer
import xyz.upperlevel.hgame.world.entity.Entity

class NoAttackBehaviour(layer: BehaviourLayer, entity: Entity) : Behaviour(layer, "none", entity) {
    override fun initialize() {
        hook({ Gdx.input.isKeyJustPressed(Input.Keys.SPACE) }, layer["attack"]!!)
        hook({ Gdx.input.isKeyJustPressed(Input.Keys.J) }, layer["special_attack"]!!)
    }
}