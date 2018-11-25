package xyz.upperlevel.hgame.input;

import com.badlogic.gdx.Input;

@FunctionalInterface
public interface InputTrigger {
    boolean check(Input input);

    default InputTrigger and(InputTrigger other) {
        return i -> check(i) && other.check(i);
    }

    default InputTrigger or(InputTrigger other) {
        return i -> check(i) || other.check(i);
    }

    /**
     * Returns a InputTrigger that triggers every frame that the key is pressed
     * @param key the pressed key
     * @return the equivalent InputTrigger
     */
    static InputTrigger onKeyDown(int key) {
        return in -> in.isKeyPressed(key);
    }

    /**
     * Returns a InputTrigger that triggers only when the button has just been pressed.
     * @param key the pressed key
     * @return the equivalent InputTrigger
     */
    static InputTrigger onKeyPress(int key) {
        return in -> in.isKeyJustPressed(key);
    }
}
