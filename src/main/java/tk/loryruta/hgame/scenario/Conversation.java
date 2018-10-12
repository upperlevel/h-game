package tk.loryruta.hgame.scenario;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.Getter;
import tk.loryruta.hgame.scenario.character.Character;

import java.util.ArrayList;
import java.util.List;

public class Conversation {
    @Getter
    private final Character speaker;

    private List<Sentence> sentences;

    @Getter
    private Sentence current;
    private int currentIndex;

    public Conversation(Character speaker, JsonArray json) {
        this.speaker = speaker;

        sentences = new ArrayList<>();
        json.forEach(sub -> sentences.add(new Sentence(sub.getAsJsonObject())));

        current = sentences.get(currentIndex);
        currentIndex = 0;
    }

    public Sentence next() {
        currentIndex = (currentIndex + 1) % sentences.size();
        current = sentences.get(currentIndex);
        return current;
    }

    public class Sentence {
        @Getter
        private String text;

        @Getter
        private Sound audio;

        public Sentence(JsonObject json) {
            text = json.getAsJsonPrimitive("text").getAsString();
            String path = json.getAsJsonPrimitive("audio").getAsString();
            audio = Gdx.audio.newSound(Gdx.files.internal(path));

        }
    }
}
