package tk.loryruta.hgame.scenario.character;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public final class SpriteExtractor {
    public static TextureRegion[] horizontal(Texture texture, int width, int height, int y, int offset, int count) {
        TextureRegion[] regions = new TextureRegion[count];
        float regY = y / (float) height;
        float regWidth = 1.0f / (float) width;
        float regHeight = 1.0f / (float) height;
        for (int i = 0; i < count; i++) {
            float regX =  i * regWidth + offset;
            regions[i] = new TextureRegion(
                    texture,
                    regX,
                    regY,
                    regX + regWidth,
                    regY + regHeight
            );
        }
        return regions;
    }

    public static TextureRegion[][] grid(Texture texture, int width, int height) {
        float regWidth = 1.0f / (float) width;
        float regHeight = 1.0f / (float) height;
        TextureRegion[][] regions = new TextureRegion[width][];
        for (int x = 0; x < width; x++) {
            regions[x] = new TextureRegion[height];
            for (int y = 0; y < height; y++) {
                regions[x][y] = new TextureRegion(
                        texture,
                        x * regWidth,
                        y * regHeight,
                        x * regWidth + regWidth,
                        y * regHeight + regHeight
                );
            }
        }
        return regions;
    }

    private SpriteExtractor() {
    }
}
