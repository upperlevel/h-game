package xyz.upperlevel.hgame.input

import com.badlogic.gdx.Input


typealias InputTrigger = (Input) -> Boolean

fun InputTrigger.and (other: InputTrigger): InputTrigger {
    return { this(it) && other(it) }
}

fun InputTrigger.or (other: InputTrigger): InputTrigger {
    return { this(it) || other(it) }
}

object InputTriggers {
    /**
     * Returns a InputTrigger that triggers every frame that the key is pressed
     * @param key the pressed key
     * @return the equivalent InputTrigger
     */
    fun onKeyDown(key: Int): InputTrigger {
        return { it.isKeyPressed(key) }
    }

    /**
     * Returns a InputTrigger that triggers only when the button has just been pressed.
     * @param key the pressed key
     * @return the equivalent InputTrigger
     */
    fun onKeyPress(key: Int): InputTrigger {
        return { it.isKeyJustPressed(key) }
    }
}
