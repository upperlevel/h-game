package tk.loryruta.hgame.scenario;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

public class GameOverScene extends Scenario {
    public GameOverScene() {
        Sound s = Gdx.audio.newSound(Gdx.files.internal("resources/audio/windows_xp_crash.mp3"));
        s.loop();
    }

    @Override
    public void update() {
        // No update
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Conversation.show(
                "GAME OVER",
                "You didn't manage to come back in real life."
        );
    }
}
