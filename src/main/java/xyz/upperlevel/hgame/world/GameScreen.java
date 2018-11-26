package xyz.upperlevel.hgame.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import lombok.Getter;
import xyz.upperlevel.hgame.network.Endpoint;

public class GameScreen extends ScreenAdapter {
    private WorldRenderer renderer;
    private World world;

    @Getter
    private Endpoint endpoint;

    @Override
    public void show() {
        renderer = new WorldRenderer();
        world = new World();
    }

    @Override
    public void hide() {
        Conversation.dispose();
        renderer.dispose();
    }

    public void connect(Endpoint endpoint) {
        this.endpoint = endpoint;
        world.initEndpoint(endpoint);
        endpoint.openAsync();
    }

    @Override
    public void resize(int width, int height) {
        Conversation.resize(width, height);
    }

    @Override
    public void render(float delta) {
        world.update(endpoint);

        // Render
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        renderer.render(world);
        Conversation.render();
    }
}
