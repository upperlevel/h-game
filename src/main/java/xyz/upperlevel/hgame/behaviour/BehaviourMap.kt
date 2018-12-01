package xyz.upperlevel.hgame.behaviour

import com.badlogic.gdx.scenes.scene2d.Actor

class BehaviourMap {
    val behaviours: MutableMap<String, Behaviour> = HashMap()
    var current: Behaviour? = null
        set(value) {
            field?.onLeave()
            field = value
            field?.onEnter()
        }

    companion object {
        fun standard(actor: Actor) {

        }
    }
}