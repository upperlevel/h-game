package tk.loryruta.hgame.scenario;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import lombok.Getter;

public class Conversation {
    public Conversation() {
    }

    public static class Sentence {
        @Getter
        private String text;

        @Getter
        private Sound audio;

        public Sentence(String text, String audioPath) {
            this.text = text;
            try {
                audioPath = "resources/audio/" + audioPath;
                audio = Gdx.audio.newSound(Gdx.files.internal(audioPath));
            } catch (Exception e) {
                System.err.println("Can't find audio file: " + audioPath);
                audio = null;
            }

        }
    }
}
