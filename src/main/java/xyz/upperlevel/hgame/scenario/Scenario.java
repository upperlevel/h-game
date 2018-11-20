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
import xyz.upperlevel.hgame.scenario.character.impl.Santinelli;

import java.util.ArrayList;
import java.util.List;

public class Scenario {
    public static final float ACTOR_SPEED = 0.05f;

    public final Actor player;
    public final List<Actor> actors = new ArrayList<>();

    private boolean frozen = false;

    public float height;
    public float gravity;

    public float groundHeight;
    public Color groundColor = Color.DARK_GRAY;

    public Scenario() {
        height = 5.0f;
        gravity = 9.8f;
        groundHeight = 1.0f;

        player = new Santinelli().personify();
        spawn(player);
    }

    public void freeze(boolean flag) {
        frozen = flag;
    }

    public void spawn(Actor actor) {
        actors.add(actor);
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
            if (Gdx.input.isKeyJustPressed(Keys.SPACE)) {
                player.attack();
            }
        }
        actors.forEach(human -> human.update(this));
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

        // the player will be the one over all the other extras
        for (int i = actors.size() - 1; i >= 0; i--) {
            actors.get(i).render();
        }

        onRender();
        batch.end();
    }

    public void onRender() {
    }
}
