package tk.loryruta.hgame.scenario.character;

import com.badlogic.gdx.math.Vector2;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import tk.loryruta.hgame.scenario.Conversation;
import tk.loryruta.hgame.scenario.Scenario;
import tk.loryruta.hgame.scenario.scheduler.Scheduler;

public class Character {
    public static final float WIDTH = Body.WIDTH;
    public static final float HEIGHT = Body.HEIGHT + 1.0f; // Head.HEIGHT

    @Getter
    private final Scenario scenario;

    @Getter
    @Setter
    private String name;

    @Getter
    private float x, y;

    @Getter
    private Head head;

    @Getter
    private Body body;

    @Getter
    private boolean left;

    @Getter
    private Vector2 velocity = new Vector2();

    private Conversation conversation;

    private int speakingAnimationTask = -1;

    public Character(Scenario scenario, JsonObject json) {
        this.scenario = scenario;

        name = json.getAsJsonPrimitive("name").getAsString();
        x = json.getAsJsonPrimitive("x").getAsFloat();
        y = json.getAsJsonPrimitive("y").getAsFloat();
        head = new Head(this, json.getAsJsonPrimitive("head").getAsString());
        body = new Body(this, json.getAsJsonPrimitive("body").getAsString());
        conversation = new Conversation(this, json.getAsJsonArray("conversation"));
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void setVelocity(float velocityX, float velocityY) {
        velocity.set(velocityX, velocityY);
    }

    public float getMinX() {
        return x;
    }

    public float getMinY() {
        return y;
    }

    public float getMaxX() {
        return x + WIDTH;
    }

    public float getMaxY() {
        return y + HEIGHT;
    }

    /**
     * Checks if the given human is in this human bounding box with at least one point.
     */
    public boolean intersect(Character other) {
        return (getMinX() >= other.getMinX() && getMinX() <= other.getMaxX()) ||
                (getMinY() >= other.getMinY() && getMinY() <= other.getMaxY()) ||
                (getMaxX() >= other.getMinX() && getMaxX() <= other.getMaxX()) ||
                (getMaxY() >= other.getMinY() && getMaxY() <= other.getMaxY());
    }

    public void speak(Character with) {
        if (speakingAnimationTask == -1) {
            Conversation.Sentence sentence = conversation.next();
            scenario.setRenderingSentence(this, sentence);
            sentence.getAudio().play(1.0f);

            left = with.x - x < 0;
            getBody().setPunching(1);
            speakingAnimationTask = Scheduler.start(() -> {
                getBody().setIdle();
                speakingAnimationTask = -1;
            }, 1000);
        }
    }

    public void move(float offsetX, float offsetY) {
        if (offsetX != 0 || offsetY != 0) {
            left = offsetX < 0;
            x += offsetX;
            y += offsetY;
        }
        head.onMove(offsetX, offsetY);
        body.onMove(offsetX, offsetY);
    }

    public void punch() {
        body.onPunch();
    }

    public void update(Scenario scenario, float delta) {
        velocity.y -= scenario.getGravity() * delta;

        /*
        // gravity
        x += velocity.x * delta;
        y += velocity.y * delta;

        float ground = scenario.getGroundHeight();
        if ( y < ground) {
            y = ground;
            velocity.y = 0;
        }
        */
    }

    public void render() {
        body.onRender();
        head.onRender();
    }
}
