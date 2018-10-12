package tk.loryruta.hgame.scenario.character;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import lombok.Getter;
import tk.loryruta.hgame.HGame;

public class Head {
    @Getter
    private final Character character;

    @Getter
    private final Sprite sprite;

    public Head(Character character, String path) {
        this.character = character;

        Texture texture = new Texture(Gdx.files.internal(path));
        sprite = new Sprite(texture);
        sprite.setSize(1, 1);
    }

    public void onMove(float offsetX, float offsetY) {
    }

    public void onRender() {
        if (character.isLeft() != getSprite().isFlipX()) {
            sprite.flip(true, false);
        }
        sprite.setPosition(character.getX(), character.getY() + Body.HEIGHT - 0.4f);
        sprite.draw(HGame.instance.getBatch());
    }
}
