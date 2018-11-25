package xyz.upperlevel.hgame.scenario;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import lombok.Getter;
import xyz.upperlevel.hgame.GameProtocol;
import xyz.upperlevel.hgame.network.Client;
import xyz.upperlevel.hgame.network.Endpoint;
import xyz.upperlevel.hgame.network.Server;
import xyz.upperlevel.hgame.scenario.scheduler.Scheduler;
import xyz.upperlevel.hgame.scenario.sequence.Sequence;

import java.net.InetAddress;

public class GameScreen extends ScreenAdapter {
    public static GameScreen instance;

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
    public void show() {
        instance = this;

        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        camera = new OrthographicCamera();

        scenario = new Scenario();
    }

    public void connect(InetAddress address, String nick, boolean master) {
        if (master) {
            Server server = new Server(GameProtocol.PROTOCOL, GameProtocol.GAME_PORT);
            server.openAsync();
            endpoint = server;
        } else {
            Client client = new Client(GameProtocol.PROTOCOL, address, GameProtocol.GAME_PORT);
            client.openAsync(true);
            endpoint = client;
        }
        scenario.initEndpoint(endpoint);
    }

    @Override
    public void resize(int width, int height) {
        Conversation.resize(width, height);
    }

    @Override
    public void render(float delta) {
        scenario.update();
        Scheduler.update(); // Scheduler API update
        Sequence.updateAll(); // Sequence API update
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
