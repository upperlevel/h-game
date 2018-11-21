package xyz.upperlevel.hgame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import lombok.Getter;
import xyz.upperlevel.hgame.network.DisconnectedEndpoint;
import xyz.upperlevel.hgame.network.Endpoint;
import xyz.upperlevel.hgame.scenario.Conversation;
import xyz.upperlevel.hgame.scenario.Event;
import xyz.upperlevel.hgame.scenario.Scenario;
import xyz.upperlevel.hgame.scenario.animation.Sequence;
import xyz.upperlevel.hgame.scenario.scheduler.Scheduler;

public class HGame extends ApplicationAdapter {

    public static void main(String[] args) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

        config.title = "H-Game";
        config.width = 720;
        config.height = 720;

        new LwjglApplication(new HGame(), config);
    }

    public static HGame instance;

    @Getter
    private OrthographicCamera camera;

    @Getter
    private SpriteBatch batch;

    @Getter
    private ShapeRenderer shapeRenderer;

    @Getter
    private Scenario scenario;

    @Getter
    private Endpoint endpoint;

    @Override
    public void create() {
        instance = this;

        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        camera = new OrthographicCamera();

        scenario = new Scenario();

        endpoint = new DisconnectedEndpoint(null);
    }

    @Override
    public void resize(int width, int height) {
        Conversation.resize(width, height);
    }

    @Override
    public void render() {
        scenario.update();
        Scheduler.update(); // Scheduler API update
        Sequence.update(); // Sequence API update
        Event.update();

        // Render
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        scenario.render();
        Conversation.render();
    }

    @Override
    public void dispose() {
        Conversation.dispose();
        batch.dispose();
    }
}
