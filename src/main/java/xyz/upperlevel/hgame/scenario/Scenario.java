package xyz.upperlevel.hgame.scenario;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.upperlevel.hgame.scenario.character.Actor;
import xyz.upperlevel.hgame.scenario.character.Character;
import xyz.upperlevel.hgame.scenario.character.impl.Santy;
import xyz.upperlevel.hgame.scenario.character.impl.Sfera;

import java.util.ArrayList;
import java.util.List;

public class Scenario {
    public static final Logger logger = LogManager.getLogger();
    public static final float ACTOR_MOVE_SPEED = 0.05f;
    public static final float ACTOR_JUMP_SPEED = 2f;

    public Actor player;
    public final List<Actor> extras = new ArrayList<>();

    private boolean frozen = false;

    public float height;
    public float gravity;

    public float groundHeight;
    public Color groundColor = Color.DARK_GRAY;

    // Only done to allow character swapping.
    private List<Character> characters = new ArrayList<>();
    private int currentCharacter = 0;

    public Scenario() {
        height = 5.0f;
        gravity = 9.8f;
        groundHeight = 1.0f;

        characters.add(new Santy());
        characters.add(new Sfera());

        player = characters.get(currentCharacter++).personify(-1);
    }

    private void changeCharacter(Character character) {
        Actor changed = character.personify(-1);
        changed.x = player.x;
        changed.y = player.y;
        changed.setVelocity(player.getVelocity());
        player = changed;
    }

    public void freeze(boolean flag) {
        frozen = flag;
    }

    public void spawnExtra(Actor actor) {
        extras.add(actor);
    }

    public void updatePlayer() {
        Input input = Gdx.input;
        for (var action : player.getInput().getActions()) {
            if (!action.getTrigger().check(player, input)) continue;
            action.trigger();
        }
    }

    public void update() {
        if (!frozen) {
            updatePlayer();

            if (Gdx.input.isKeyJustPressed(Keys.RIGHT)) {
                Character current = characters.get(currentCharacter);
                changeCharacter(current);
                currentCharacter = (currentCharacter + 1) % characters.size();
                logger.info("Character changed to: %s", current.getName());
            }
        }

        // Updates physics first for extras then for the player.
        extras.forEach(extra -> extra.update(this));
        player.update(this);
    }

    public void render() {
        // camera
        OrthographicCamera camera = GameScreen.instance.getCamera();
        SpriteBatch batch = GameScreen.instance.getBatch();
        ShapeRenderer renderer = GameScreen.instance.getShapeRenderer();

        camera.setToOrtho(false, Gdx.graphics.getWidth() / (float) Gdx.graphics.getHeight() * height, height);
        camera.position.x = player.getX() + Actor.WIDTH / 2f;
        camera.position.y = height / 2f + (player.getY() - groundHeight);
        camera.update();

        batch.setProjectionMatrix(camera.combined);
        renderer.setProjectionMatrix(camera.combined);

        // ground
        renderer.setColor(groundColor);
        renderer.begin(ShapeType.Filled);
        renderer.rect(player.getX() - 10, 0, 20, groundHeight + 1);
        renderer.end();

        // humans
        batch.begin();

        // Firstly we render the extras that should be behind of the scene.
        for (Actor actor : extras) {
            actor.render();
        }

        // The player is not part of the other actors.
        player.render();

        onRender();
        batch.end();
    }

    public void onRender() {
    }
}
