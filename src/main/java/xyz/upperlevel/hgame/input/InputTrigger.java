package xyz.upperlevel.hgame.input;

import com.badlogic.gdx.Input;
import xyz.upperlevel.hgame.scenario.character.Actor;

@FunctionalInterface
public interface InputTrigger {
    boolean check(Actor actor, Input input);

    default InputTrigger and(InputTrigger other) {
        return (a, i) -> check(a, i) && other.check(a, i);
    }

    default InputTrigger or(InputTrigger other) {
        return (a, i) -> check(a, i) || other.check(a, i);
    }

    /**
     * Returns a InputTrigger that triggers every frame that the key is pressed
     * @param key the pressed key
     * @return the equivalent InputTrigger
     */
    static InputTrigger onKeyDown(int key) {
        return (a, in) -> in.isKeyPressed(key);
    }

    /**
     * Returns a InputTrigger that triggers only when the button has just been pressed.
     * @param key the pressed key
     * @return the equivalent InputTrigger
     */
    static InputTrigger onKeyPress(int key) {
        return (a, in) -> in.isKeyJustPressed(key);
    }
}
