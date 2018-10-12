package tk.loryruta.hgame.scenario.character;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public final class SpriteExtractor {
    public static TextureRegion[] horizontal(Texture texture, int width, int height, int y, int offset, int count) {
        TextureRegion[] frames = new TextureRegion[count];
        float regY = y / (float) height;
        float regWidth = 1.0f / (float) width;
        float regHeight = 1.0f / (float) height;
        for (int i = 0; i < count; i++) {
            float regX =  i * regWidth + offset;
            frames[i] = new TextureRegion(
                    texture,
                    regX,
                    regY,
                    regX + regWidth,
                    regY + regHeight
            );
        }
        return frames;
    }

    private SpriteExtractor() {
    }
}
