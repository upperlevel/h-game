package tk.loryruta.hgame.scenario;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.Getter;
import tk.loryruta.hgame.HGame;
import tk.loryruta.hgame.scenario.character.Body;
import tk.loryruta.hgame.scenario.character.Character;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.out;

public class Scenario {
    public static final float CONTROL_SENSITIVITY = 0.05f;
    public static final float JUMP_STRENGTH = 5.0f;

    @Getter
    private int width, height;

    @Getter
    private Sprite background;

    @Getter
    private float groundHeight;

    @Getter
    private float gravity;

    @Getter
    private ConversationRenderer conversationRenderer;

    @Getter
    private Character actor;
    private List<Character> extras = new ArrayList<>();


    public Scenario(JsonObject json) {
        width = json.getAsJsonPrimitive("width").getAsInt();
        height = json.getAsJsonPrimitive("height").getAsInt();


        String path = json.getAsJsonPrimitive("background").getAsString();
        Texture texture = new Texture(Gdx.files.internal(path));
        background = new Sprite(texture);
        background.setPosition(0, 0);
        background.setSize(width, height);

        groundHeight = json.getAsJsonPrimitive("groundHeight").getAsFloat();
        gravity = json.getAsJsonPrimitive("gravity").getAsFloat();

        actor = new Character(this, HGame.json(json.getAsJsonPrimitive("actor").getAsString()));

        json.getAsJsonArray("extras").forEach(extra -> {
            extras.add(new Character(Scenario.this, HGame.json(extra.getAsString())));
        });

        conversationRenderer = new ConversationRenderer();
    }

    /**
     * Gets the extra the actor is conversing with.
     */
    public Character getListener() {
        for (Character extra : extras) {
            if (actor.intersect(extra)) {
                return extra;
            }
        }
        return null;
    }

    public void setRenderingSentence(Character speaker, Conversation.Sentence sentence) {
        conversationRenderer.setSentence(speaker, sentence);
    }

    public void update(float delta) {
        /* Actor movement */
        float offX = 0.0f;
        float offY = 0.0f;

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            offX = -CONTROL_SENSITIVITY;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            offX = CONTROL_SENSITIVITY;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            offY = -CONTROL_SENSITIVITY;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            offY = CONTROL_SENSITIVITY;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.I)) {
            offY = CONTROL_SENSITIVITY;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.J)) {
            offY = CONTROL_SENSITIVITY;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.H)) {
            actor.setVelocity(0, JUMP_STRENGTH);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            Character recipient = getListener();
            if (recipient != null) {
                System.out.println("TALKING");
                recipient.speak(actor);
            } else {
                System.out.println("NOT TALKING");
            }
        }

        /* Debug */
        if (Gdx.input.isKeyPressed(Input.Keys.TAB)) {
            out.println("Actor information:");
            out.println("Velocity: " + actor.getVelocity());
            out.println("Position: (" + actor.getX() + " " + actor.getY() + ")");
        }

        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            actor.punch();
        }
        actor.move(offX, offY);

        /* Update */
        actor.update(this, delta);
    }

    public void render() {
        OrthographicCamera camera = HGame.instance.getCamera();
        SpriteBatch batch = HGame.instance.getBatch();

        camera.setToOrtho(false, Gdx.graphics.getWidth() / (float) Gdx.graphics.getHeight() * height, height);
        camera.position.x = actor.getX() + Body.WIDTH / 2f;
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        // World
        batch.begin();

        background.draw(batch);

        for (Character extra : extras) {
            extra.render();
        }

        actor.render();

        batch.end();


        // GUI
        // todo remove conversation.render();
        conversationRenderer.render();
    }

    public static Scenario from(String path) {
        try {
            return new Scenario(new Gson().fromJson(new FileReader(path), JsonObject.class));
        } catch (FileNotFoundException e) {
            throw new IllegalStateException("Can't find file: " + path, e);
        }
    }
}
