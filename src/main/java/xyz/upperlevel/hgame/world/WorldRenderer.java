package xyz.upperlevel.hgame.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import lombok.Getter;
import xyz.upperlevel.hgame.world.character.Actor;

public class WorldRenderer {
    @Getter
    private OrthographicCamera camera;

    @Getter
    private SpriteBatch spriteBatch;

    @Getter
    private ShapeRenderer shapeRenderer;

    public WorldRenderer() {
        init();
    }

    private void init() {
        camera = new OrthographicCamera();

        spriteBatch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
    }

    public void render(World world) {
        if (!world.isReady()) return;
        float height = world.getHeight();
        float groundHeight = world.getGroundHeight();
        Actor player = world.getPlayer();

        camera.setToOrtho(false, Gdx.graphics.getWidth() / (float) Gdx.graphics.getHeight() * height, height);

        // Moves the camera to the position of the player.
        camera.position.x = player.getX() + Actor.WIDTH / 2f;
        camera.position.y = height / 2f + (player.getY() - groundHeight);
        camera.update();

        // Applies the camera to both the renderers.
        spriteBatch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);

        // Draws the ground before all.
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.rect(player.getX() - 10, 0, 20, groundHeight + 1);
        shapeRenderer.end();

        spriteBatch.begin();
        world.getEntities().forEach(actor -> actor.render(this));
        spriteBatch.end();
    }

    public void dispose() {
        spriteBatch.dispose();
        shapeRenderer.dispose();
    }
}
