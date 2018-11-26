package xyz.upperlevel.hgame.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import xyz.upperlevel.hgame.DefaultFont;
import xyz.upperlevel.hgame.HGame;

public class LoginScreen extends ScreenAdapter {
    public static final String ACCEPTED_NAME_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789@#<>!_-";

    private Stage stage;
    private Skin skin;

    public LoginScreen() {
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

        // Configure a TextButtonStyle and name it "default". Skin resources are stored by type, so this doesn't overwrite the font.
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = skin.newDrawable("white", Color.DARK_GRAY);
        textButtonStyle.down = skin.newDrawable("white", Color.NAVY);
        textButtonStyle.disabled = skin.newDrawable("white", Color.RED);
        textButtonStyle.over = skin.newDrawable("white", Color.LIGHT_GRAY);
        textButtonStyle.font = skin.getFont("default");
        skin.add("default", textButtonStyle);

        var textFieldStyle = new TextField.TextFieldStyle();
        textFieldStyle.font = skin.getFont("default");
        textFieldStyle.fontColor = Color.SKY;
        textFieldStyle.cursor = skin.newDrawable("white", Color.GRAY);

        skin.add("default", textFieldStyle);

        // Create a table that fills the screen. Everything else will go inside this table.
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        final var username = new TextField("", skin);
        username.setMessageText("Username");
        username.setAlignment(Align.center);
        username.setMaxLength(50);
        username.setTextFieldFilter((textField, c) -> ACCEPTED_NAME_CHARS.indexOf(c) >= 0);
        table.add(username).growX().row();

        var playButton = new TextButton("Play", skin);
        playButton.setDisabled(true);
        table.add(playButton).pad(5.0f).width(100).row();

        var trainButton = new TextButton("Train", skin);
        trainButton.setDisabled(true);
        table.add(trainButton).pad(5.0f).width(100).row();

        username.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                boolean invalid = username.getText().isEmpty();
                playButton.setDisabled(invalid);
                trainButton.setDisabled(invalid);
            }
        });

        playButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (playButton.isPressed()) {
                    HGame.get().setScreen(new MatchMakingScreen(HGame.get().getDiscovery(), username.getText()));
                }
            }
        });

        trainButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (trainButton.isPressed()) {
                    HGame.get().setScreen(new TrainScreen());
                }
            }
        });
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
