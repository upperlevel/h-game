package xyz.upperlevel.hgame.world.sequence

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input

/**
 * It's used by the update cycle to check periodically if some condition has been verified.
 * If returns `true`, the event can be executed. Otherwise the event can wait.
 */
typealias Trigger = () -> Boolean

object Triggers {
    val NONE = { true }
    val ENTER_KEY = { Gdx.input.isKeyJustPressed(Input.Keys.ENTER)}

    fun sleep(duration: Long): Trigger {
        val now = System.currentTimeMillis()
        return { System.currentTimeMillis() >= now + duration }
    }
}
