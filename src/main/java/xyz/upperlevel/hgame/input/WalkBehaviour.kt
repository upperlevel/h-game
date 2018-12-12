package xyz.upperlevel.hgame.input

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.math.Vector2
import xyz.upperlevel.hgame.world.character.Entity
import xyz.upperlevel.hgame.world.sequence.Sequence

open class WalkBehaviour(behaviourMap: BehaviourMap, id: String, entity: Entity) : Behaviour(behaviourMap, id, entity) {
    private var animation: Sequence? = null

    override fun initialize() {
        Behaviour.addDefault(entity, this)
    }

    override fun onEnable() {
        animation = Sequence.create().oscillate({ _, time -> entity.setFrame(time, 1) }, 200, 3).play()
    }

    override fun onDisable() {
        animation?.dismiss()
        entity.moveForce = null
    }
}

class WalkLeftBehaviour(behaviourMap: BehaviourMap, entity: Entity) : WalkBehaviour(behaviourMap, "walk_left", entity) {
    override fun initialize() {
        super.initialize()
        hook({ !Gdx.input.isKeyPressed(Input.Keys.A) }, behaviourMap["idle"]!!)
    }

    override fun onEnable() {
        super.onEnable()
        entity.moveForce = Vector2(-1f, 0f)
    }
}

class WalkRightBehaviour(behaviourMap: BehaviourMap, entity: Entity) : WalkBehaviour(behaviourMap, "walk_right", entity) {
    override fun initialize() {
        super.initialize()
        hook({ !Gdx.input.isKeyPressed(Input.Keys.D) }, behaviourMap["idle"]!!)
    }

    override fun onEnable() {
        super.onEnable()
        entity.moveForce = Vector2(1f, 0f)
    }
}
