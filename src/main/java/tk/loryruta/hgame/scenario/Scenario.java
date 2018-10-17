package tk.loryruta.hgame.scenario;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import tk.loryruta.hgame.HGame;
import tk.loryruta.hgame.scenario.character.Human;

import java.util.ArrayList;
import java.util.List;

public class Scenario {
    public static final float ACTOR_SPEED = 0.05f;

    public final Human player;
    public final List<Human> humans = new ArrayList<>();

    private boolean frozen = false;

    public float height;
    public float gravity;

    public float groundHeight;
    public Color groundColor = Color.BLACK;

    public Scenario() {
        height = 5.0f;
        gravity = 9.8f;
        groundHeight = 1.0f;

        spawn(player = new Human("Player", "images/dystopian_john.png"));
    }

    public void freeze(boolean flag) {
        frozen = flag;
    }

    public void spawn(Human human) {
        humans.add(human);
    }

    public Human getTalkingWith(Human who) {
        for (Human human : humans) {
            if (who != human && who.intersect(human)) {
                return human;
            }
        }
        return null;
    }

    public void update() {
        if (!frozen) {
            if (Gdx.input.isKeyPressed(Keys.A)) {
                player.move(-ACTOR_SPEED, 0);
            }
            if (Gdx.input.isKeyPressed(Keys.D)) {
                player.move(ACTOR_SPEED, 0);
            }
            if (Gdx.input.isKeyPressed(Keys.W)) {
                player.move(0, ACTOR_SPEED);
            }
            if (Gdx.input.isKeyPressed(Keys.S)) {
                player.move(0, -ACTOR_SPEED);
            }
            if (Gdx.input.isKeyJustPressed(Keys.E)) {
                Human with = getTalkingWith(player);
                if (with != null) {
                    with.talk(player);
                }
            }
        }
        humans.forEach(human -> human.update(this));
    }

    public void render() {
        // camera
        OrthographicCamera camera = HGame.instance.getCamera();
        SpriteBatch batch = HGame.instance.getBatch();
        ShapeRenderer renderer = HGame.instance.getShapeRenderer();

        camera.setToOrtho(false, Gdx.graphics.getWidth() / (float) Gdx.graphics.getHeight() * height, height);
        camera.position.x = player.getX() + Human.WIDTH / 2f;
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

        // the player will be the one over all the other extras
        for (int i = humans.size() - 1; i >= 0; i--) {
            humans.get(i).render();
        }

        onRender();
        batch.end();
    }

    public void onRender() {
    }
}
