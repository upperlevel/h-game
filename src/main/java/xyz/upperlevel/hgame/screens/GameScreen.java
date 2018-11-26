package xyz.upperlevel.hgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import lombok.Getter;
import xyz.upperlevel.hgame.network.Endpoint;
import xyz.upperlevel.hgame.world.Conversation;
import xyz.upperlevel.hgame.world.World;
import xyz.upperlevel.hgame.world.WorldRenderer;

public class GameScreen extends ScreenAdapter {
    private WorldRenderer renderer;
    private World world = new World();

    @Getter
    private Endpoint endpoint;

    @Override
    public void show() {
        renderer = new WorldRenderer();
    }

    @Override
    public void hide() {
        Conversation.dispose();
        renderer.dispose();
    }

    public void connect(Endpoint endpoint) {
        this.endpoint = endpoint;
        world.initEndpoint(endpoint);
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
