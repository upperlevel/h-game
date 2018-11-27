package xyz.upperlevel.hgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.upperlevel.hgame.DefaultFont;
import xyz.upperlevel.hgame.HGame;
import xyz.upperlevel.hgame.event.EventListener;
import xyz.upperlevel.hgame.network.Endpoint;
import xyz.upperlevel.hgame.network.events.ConnectionOpenEvent;

import static xyz.upperlevel.hgame.GdxUtil.runSync;

public class WaitingConnectionScreen extends ScreenAdapter {
    private static final Logger logger = LogManager.getLogger();
    private static final float BLINK_DELTA = 1.0f;
    // Wait for connection (both client and server) and add a "cancel" button that returns to the SelectHostScene

    private Endpoint endpoint;
    private Screen nextScreen;

    private Stage stage;
    private Skin skin;
    private Label text;

    private int points = 0;
    private float gdelta = 0.0f;

    public WaitingConnectionScreen(Endpoint endpoint, Screen nextScreen) {
        this.endpoint = endpoint;
        this.nextScreen = nextScreen;
        init();
    }

    public void init() {
        stage = new Stage(new ScreenViewport());

        // A skin can be loaded via JSON or defined programmatically, either is fine. Using a skin is optional but strongly
        // recommended solely for the convenience of getting a texture, region, etc as a drawable, tinted drawable, etc.
        skin = new Skin();

        // Generate a 1x1 white texture and store it in the skin named "white".
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        skin.add("white", new Texture(pixmap));

        skin.add("default", DefaultFont.FONT);

        var labelStyle = new Label.LabelStyle();
        labelStyle.font = skin.getFont("default");
        labelStyle.background = skin.newDrawable("white", Color.CLEAR);
        skin.add("default", labelStyle);


        var textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = skin.newDrawable("white", Color.DARK_GRAY);
        textButtonStyle.down = skin.newDrawable("white", Color.RED);
        textButtonStyle.over = skin.newDrawable("white", Color.LIGHT_GRAY);
        textButtonStyle.font = skin.getFont("default");
        skin.add("default", textButtonStyle);


        var table = new Table(skin);
        table.setFillParent(true);

        text = new Label("Connecting", skin);
        text.setAlignment(Align.center);

        table.add(text).growX().row();

        var button = new TextButton("Cancel", skin);
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                logger.warn("CANCEL");
            }
        });

        table.add(button).row();

        stage.addActor(table);

        endpoint.getEvents().register(EventListener.listener(ConnectionOpenEvent.class, e -> {
            runSync(() -> HGame.get().setScreen(nextScreen));
        }));
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        gdelta += delta;

        if (gdelta >= BLINK_DELTA) {
            gdelta -= BLINK_DELTA;
            points = (points + 1) % 4;
            String str = "Connecting...";
            text.setText(str.substring(0, str.length() - (3 - points)));
        }

        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }
}
