package xyz.upperlevel.hgame.scenario;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import xyz.upperlevel.hgame.event.EventListener;
import xyz.upperlevel.hgame.input.InputAction;
import xyz.upperlevel.hgame.network.Endpoint;
import xyz.upperlevel.hgame.network.Server;
import xyz.upperlevel.hgame.network.events.ConnectionOpenEvent;
import xyz.upperlevel.hgame.scenario.character.Actor;
import xyz.upperlevel.hgame.scenario.character.impl.Santy;
import xyz.upperlevel.hgame.scenario.entity.EntityRegistry;

public class Scenario {
    public static final float ACTOR_MOVE_SPEED = 0.05f;
    public static final float ACTOR_JUMP_SPEED = 2f;

    public float height;
    public float gravity;

    public float groundHeight;
    public Color groundColor = Color.DARK_GRAY;

    private EntityRegistry entityRegistry = new EntityRegistry();
    private Actor player;

    private boolean isMaster;

    public Scenario() {
        height = 5.0f;
        gravity = 9.8f;
        groundHeight = 1.0f;
    }

    public void onGameStart() {
        var x = 20 / 4;
        if (isMaster) x += 20 / 2;
        player = entityRegistry.spawn(Santy.class, x, groundHeight, isMaster);
    }

    public void update() {
        // Inputs
        var input = Gdx.input;
        player.getInput()
                .getActions()
                .stream()
                .filter(a -> a.getTrigger().check(input))
                .forEach(InputAction::trigger);

        // Updates
        entityRegistry.getEntities().forEach(e -> e.update(this));
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
        entityRegistry.getEntities().forEach(Actor::render);

        onRender();
        batch.end();
    }

    public void onRender() {
    }

    public void initEndpoint(Endpoint endpoint) {
        isMaster = endpoint instanceof Server;
        entityRegistry.registerType(Santy.class, new Santy()::personify);

        entityRegistry.initEndpoint(endpoint);

        endpoint.getEvents().register(EventListener.listener(ConnectionOpenEvent.class, e -> {
            onGameStart();
        }));
    }
}
