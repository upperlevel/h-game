package xyz.upperlevel.hgame.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import xyz.upperlevel.hgame.network.DisconnectedEndpoint;
import xyz.upperlevel.hgame.network.Endpoint;

public class TrainScreen extends ScreenAdapter {
    private Endpoint endpoint;

    private World world;
    private WorldRenderer renderer;

    @Override
    public void show() {
        // Fake endpoint. We don't need network for the training session.
        endpoint = new DisconnectedEndpoint();

        world = new World();
        world.initEndpoint(endpoint);
        world.onGameStart();

        renderer = new WorldRenderer();
    }

    @Override
    public void hide() {
        renderer.dispose();
    }

    @Override
    public void render(float delta) {
        // Update
        world.update(endpoint);

        // Render
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        renderer.render(world);
    }
}
