package xyz.upperlevel.hgame.input

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.math.Vector2
import xyz.upperlevel.hgame.world.character.Entity

class WalkRightBehaviour(behaviourMap: BehaviourMap, entity: Entity) : Behaviour(behaviourMap, "walk_right", entity) {
    override fun initialize() {
        hook({ !Gdx.input.isKeyPressed(Input.Keys.D) }, behaviourMap["idle"]!!)
        Behaviour.addDefault(entity, this)
    }

    override fun onEnable(): Behaviour? {
        entity.moveForce = Vector2(1f,  0f)
        return super.onEnable()
    }

    override fun onDisable() {
        super.onDisable()
        entity.moveForce = null
    }
}
