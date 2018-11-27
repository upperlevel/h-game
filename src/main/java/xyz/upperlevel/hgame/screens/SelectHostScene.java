package xyz.upperlevel.hgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import xyz.upperlevel.hgame.DefaultFont;
import xyz.upperlevel.hgame.GameProtocol;
import xyz.upperlevel.hgame.HGame;
import xyz.upperlevel.hgame.network.Client;
import xyz.upperlevel.hgame.network.Endpoint;
import xyz.upperlevel.hgame.network.Server;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class SelectHostScene extends ScreenAdapter {
    // Rendering
    private Stage stage;
    private Skin skin;

    public SelectHostScene() {
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
        textButtonStyle.checked = skin.newDrawable("white", Color.SKY);
        textButtonStyle.over = skin.newDrawable("white", Color.LIGHT_GRAY);
        textButtonStyle.font = skin.getFont("default");
        skin.add("default", textButtonStyle);

        TextFieldStyle textFieldStyle = new TextFieldStyle();
        textFieldStyle.font = skin.getFont("default");
        textFieldStyle.fontColor = Color.SKY;
        textFieldStyle.cursor = skin.newDrawable("white", Color.GRAY);

        skin.add("default", textFieldStyle);


        Table table = new Table(skin);
        table.setFillParent(true);

        TextButton clientButton = new TextButton("Client", skin);
        TextButton serverButton = new TextButton("Server", skin);

        ButtonGroup btnGroup = new ButtonGroup<>(clientButton, serverButton);

        table.add(clientButton);
        table.add(serverButton);
        table.row();

        TextField ipField = new TextField("", skin);
        ipField.setMessageText("host");
        ipField.setDisabled(true);

        table.add(ipField);

        TextField portField = new TextField(Integer.toString(GameProtocol.GAME_PORT), skin);
        portField.setMessageText("port");
        portField.setTextFieldFilter((textField, c) -> Character.isDigit(c));

        table.add(portField).row();

        TextButton connect = new TextButton("Connect", skin);
        connect.setDisabled(true);

        connect.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Endpoint endpoint;

                if (clientButton.isChecked()) {
                    try {
                        endpoint = new Client(GameProtocol.PROTOCOL, InetAddress.getByName(ipField.getText()), GameProtocol.GAME_PORT);
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                        return;
                    }
                } else if (serverButton.isChecked()) {
                   endpoint = new Server(GameProtocol.PROTOCOL, Integer.parseInt(portField.getText()));
                } else {
                    return;
                }

                GameScreen game = new GameScreen();
                WaitingConnectionScreen connScreen = new WaitingConnectionScreen(endpoint, game);

                game.connect(endpoint);

                endpoint.openAsync();

                HGame.get().setScreen(connScreen);
            }
        });

        table.add(connect).row();

        ChangeListener onBtnChange = new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ipField.setDisabled(serverButton.isChecked());
                connect.setDisabled(btnGroup.getCheckedIndex() == -1);
            }
        };

        serverButton.addListener(onBtnChange);
        clientButton.addListener(onBtnChange);

        stage.addActor(table);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
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
