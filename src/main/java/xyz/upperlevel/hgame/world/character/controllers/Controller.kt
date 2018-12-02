package xyz.upperlevel.hgame.world.character.controllers

import com.badlogic.gdx.Input
import xyz.upperlevel.hgame.world.character.Entity

open class Controller protected constructor(val entity: Entity) {
    private val _actions: MutableMap<Int, () -> Unit> = HashMap()

    val actions: Map<Int, () -> Unit>
        get() = _actions

    init {
        _actions[Input.Keys.A] = { entity.control(-1f,  0f) }
        _actions[Input.Keys.D] = { entity.control( 1f,  0f) }
        _actions[Input.Keys.W] = { entity.control( 0f, -1f) }
        _actions[Input.Keys.S] = { entity.control( 0f,  1f) }
    }

    fun issue(actionType: Int) {
        _actions[actionType]?.invoke()
    }

    open fun dismiss() {
        _actions.clear()
    }

    companion object {
        fun bind(entity: Entity): Controller = Controller(entity)
    }
}