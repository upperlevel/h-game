package tk.loryruta.hgame.scenario;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;


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
        Color bg = Color.DARK_GRAY;
        Gdx.gl.glClearColor(bg.r, bg.g, bg.b, bg.a);
        Conversation.show(
                "GAME OVER",
                "You didn't manage to come back in real life."
        );
    }
}
