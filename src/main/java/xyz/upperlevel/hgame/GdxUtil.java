package xyz.upperlevel.hgame;

import com.badlogic.gdx.Gdx;

public final class GdxUtil {
    public static void runSync(Runnable r) {
        Gdx.app.postRunnable(r);
    }

    private GdxUtil() {}
}
