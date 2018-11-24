package xyz.upperlevel.hgame.scenario;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import lombok.Getter;
import lombok.Setter;
import xyz.upperlevel.hgame.DefaultFont;
import xyz.upperlevel.hgame.event.EventHandler;
import xyz.upperlevel.hgame.event.Listener;
import xyz.upperlevel.hgame.network.discovery.DiscoveryPairRequestEvent;
import xyz.upperlevel.hgame.network.discovery.DiscoveryPairResponseEvent;
import xyz.upperlevel.hgame.network.discovery.DiscoveryResponseEvent;
import xyz.upperlevel.hgame.network.discovery.UdpDiscovery;

import java.net.InetAddress;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class MatchMakingScreen extends ScreenAdapter implements Listener {
    private final UdpDiscovery discovery;
    private final BiConsumer<InetAddress, String> callback;

    @Setter
    @Getter
    private String name;
    private LinkedHashMap<InetAddress, String> players = new LinkedHashMap<>();

    // Rendering
    private Stage stage;
    private Skin skin;
    private List<String> renderPlayers;

    public MatchMakingScreen(UdpDiscovery discovery, BiConsumer<InetAddress, String> callback) {
        this.discovery = discovery;
        this.callback = callback;
        init();
    }

    public void init() {
        discovery.getEvents().register(this);

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

        var style = new List.ListStyle();
        style.font = skin.getFont("default");
        style.down = skin.newDrawable("white", Color.BLUE);
        style.selection = skin.newDrawable("white", Color.SKY);
        skin.add("default", style);


        var table = new Table(skin);
        table.setFillParent(true);

        renderPlayers = new List<>(skin);
        renderPlayers.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (!renderPlayers.getSelection().hasItems()) return;
                var selected = (Map.Entry<InetAddress, String>) players.entrySet()
                        .stream()
                        .skip(renderPlayers.getSelectedIndex())
                        .findFirst()
                        .orElse(null);
                if (selected == null) return;
                discovery.stopService();
                callback.accept(selected.getKey(), selected.getValue());
            }
        });
        renderPlayers.getSelection().setRequired(false);
        var scrollPane = new ScrollPane(renderPlayers);
        table.add(scrollPane).grow();

        stage.addActor(table);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        discovery.startService(name);
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

    @Override
    public void dispose() {
        discovery.getEvents().unregister(this);
    }

    @EventHandler
    private void onResponse(DiscoveryResponseEvent event) {
        if (players.containsKey(event.getIp())) return;
        players.put(event.getIp(), event.getNickname());
        var items = renderPlayers.getItems();
        items.add(event.getNickname());
        renderPlayers.setItems(items);
    }

    @EventHandler
    private void onPairRequest(DiscoveryPairRequestEvent event) {
        discovery.stopService();
        callback.accept(event.getIp(), event.getNickname());
    }

    @EventHandler
    private void onPairResponse(DiscoveryPairResponseEvent event) {
        discovery.stopService();
        callback.accept(event.getIp(), event.getNickname());
    }
}
