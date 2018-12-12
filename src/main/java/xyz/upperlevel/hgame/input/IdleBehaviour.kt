package xyz.upperlevel.hgame.input

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import xyz.upperlevel.hgame.world.character.Entity
import xyz.upperlevel.hgame.world.sequence.Sequence

class IdleBehaviour(behaviourMap: BehaviourMap, entity: Entity) : Behaviour(behaviourMap, "idle", entity) {
    private var animation: Sequence? = null

    override fun initialize() {
        hook({ Gdx.input.isKeyPressed(Input.Keys.A) }, behaviourMap["walk_left"]!!)
        hook({ Gdx.input.isKeyPressed(Input.Keys.D) }, behaviourMap["walk_right"]!!)
        Behaviour.addDefault(entity, this)
    }

    override fun onEnable() {
        animation = Sequence.create().repeat({ _, time ->  entity.setFrame(time % 2, 0) }, 200, -1).play()
    }

    override fun onDisable() {
        animation?.dismiss()
    }
}
