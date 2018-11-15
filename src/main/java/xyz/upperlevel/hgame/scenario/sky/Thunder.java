package xyz.upperlevel.hgame.scenario.sky;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import xyz.upperlevel.hgame.scenario.scheduler.Task;

public class Thunder {
    public static final int STEPS = 20;
    public static final Color COLOR = Color.WHITE;

    private static Sound audio;

    public static void crush(Sky sky) {
        if (audio != null) {
            audio.play();
        }
        new Task() {
            private int step;

            @Override
            public void run() {
                float ratio = Math.min(step / (float) STEPS, 1.0f);
                Color relax = sky.getRelaxColor();
                sky.setColor(new Color(
                        (ratio * relax.r) + ((1 - ratio) * COLOR.r),
                        (ratio * relax.g) + ((1 - ratio) * COLOR.g),
                        (ratio * relax.b) + ((1 - ratio) * COLOR.b),
                        1.0f
                ));
                if (++step >= STEPS) {
                    cancel();
                }
            }
        }.repeat(1);
    }

    public static void load() {
        //audio = Gdx.audio.newSound(Gdx.files.internal(""));
    }

    private Thunder() {
    }
}
