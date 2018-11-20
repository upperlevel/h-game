package xyz.upperlevel.hgame.scenario;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import xyz.upperlevel.hgame.HGame;
import xyz.upperlevel.hgame.scenario.character.Actor;
import xyz.upperlevel.hgame.scenario.character.Character;
import xyz.upperlevel.hgame.scenario.character.impl.Fera;
import xyz.upperlevel.hgame.scenario.character.impl.Santinelli;

import java.util.ArrayList;
import java.util.List;

public class Scenario {
    public static final float ACTOR_SPEED = 0.05f;

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

        characters.add(new Santinelli());
        characters.add(new Fera());

        player = characters.get(currentCharacter++).personify();
    }

    private void changeCharacter(Character character) {
        Actor changed = character.personify();
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

    public void update() {
        if (!frozen) {
            if (Gdx.input.isKeyPressed(Keys.A)) {
                player.move(-ACTOR_SPEED);
            }
            if (Gdx.input.isKeyPressed(Keys.D)) {
                player.move(ACTOR_SPEED);
            }
            if (Gdx.input.isKeyJustPressed(Keys.W)) {
                player.jump(2);
            }
            // Normal attack, just a punch.
            if (Gdx.input.isKeyJustPressed(Keys.SPACE)) {
                player.attack();
            }

            // Special attack, usually long distance.
            if (Gdx.input.isKeyJustPressed(Keys.J)) {
                player.specialAttack();
            }

            if (Gdx.input.isKeyJustPressed(Keys.RIGHT)) {
                Character current = characters.get(currentCharacter);
                changeCharacter(current);
                currentCharacter = (currentCharacter + 1) % characters.size();
                System.out.println("Character changed to: " + current.getFormalName());
            }
        }

        // Updates physics first for extras then for the player.
        extras.forEach(extra -> extra.update(this));
        player.update(this);
    }

    public void render() {
        // camera
        OrthographicCamera camera = HGame.instance.getCamera();
        SpriteBatch batch = HGame.instance.getBatch();
        ShapeRenderer renderer = HGame.instance.getShapeRenderer();

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
