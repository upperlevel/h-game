package xyz.upperlevel.hgame.world.character.controllers

import com.badlogic.gdx.Input
import xyz.upperlevel.hgame.world.character.Actor

open class Controller protected constructor(val actor: Actor) {
    private val _actions: MutableMap<Int, () -> Unit> = HashMap()

    val actions: Map<Int, () -> Unit>
        get() = _actions

    init {
        _actions[Input.Keys.A] = { actor.control(-1f,  0f) }
        _actions[Input.Keys.D] = { actor.control( 1f,  0f) }
        _actions[Input.Keys.W] = { actor.control( 0f, -1f) }
        _actions[Input.Keys.S] = { actor.control( 0f,  1f) }
    }

    fun issue(actionType: Int) {
        _actions[actionType]?.invoke()
    }

    open fun dismiss() {
        _actions.clear()
    }

    companion object {
        fun bind(actor: Actor): Controller = Controller(actor)
    }
}