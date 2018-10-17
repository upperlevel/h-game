package tk.loryruta.hgame.scenario.animation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

import java.util.function.Supplier;

/**
 * It's used by the update cycle to check periodically if some condition has been verified.
 * If returns {@code true}, the event can be executed. Otherwise the event can wait.
 */
public interface Trigger extends Supplier<Boolean> {
    Trigger NONE = () -> true;
    Trigger ENTER_KEY = () -> Gdx.input.isKeyJustPressed(Input.Keys.ENTER);

    static Trigger and(Trigger... triggers) {
        return () -> {
            for (Trigger t : triggers) {
                if (!t.get()) {
                    return false;
                }
            }
            return true;
        };
    }

    static Trigger sleep(long duration) {
        long now = System.currentTimeMillis();
        return () -> System.currentTimeMillis() >= (now + duration);
    }
}
