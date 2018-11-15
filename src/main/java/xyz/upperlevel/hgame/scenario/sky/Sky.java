package xyz.upperlevel.hgame.scenario.sky;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import lombok.Getter;
import lombok.Setter;

public class Sky {
    @Getter
    @Setter
    private Color relaxColor;

    @Getter
    @Setter
    private Color color;

    public Sky(Color relaxColor) {
        this.relaxColor = relaxColor;
        color = relaxColor;
    }

    public void render() {
        Gdx.gl.glClearColor(color.r, color.g, color.b, color.a);
    }
}
