package xyz.upperlevel.hgame.scenario.character;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import xyz.upperlevel.hgame.scenario.GameScreen;

public class Element {
    private float x, y;
    private float width, height;
    private Sprite sprite;

    public Element(float x, float y, float width, float height, String imagePath) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        Texture texture = new Texture(Gdx.files.internal(imagePath));
        sprite = new Sprite(texture);
    }

    public void render() {
        sprite.setPosition(x, y);
        sprite.setSize(width, height);
        sprite.draw(GameScreen.instance.getBatch());
    }

}
