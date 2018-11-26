package xyz.upperlevel.hgame;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import lombok.Getter;
import org.apache.logging.log4j.core.config.Configurator;
import xyz.upperlevel.hgame.network.discovery.UdpDiscovery;
import xyz.upperlevel.hgame.world.Event;
import xyz.upperlevel.hgame.screens.LoginScreen;
import xyz.upperlevel.hgame.world.scheduler.Scheduler;
import xyz.upperlevel.hgame.world.sequence.Sequence;

import java.io.IOException;

public class HGame extends Game {
    private static HGame instance;

    @Getter
    private UdpDiscovery discovery;

    @Override
    public void create() {
        instance = this;

        Configurator.initialize("config", null, Gdx.files.internal("log4j2.xml").path());

        discovery = new UdpDiscovery();

        try {
            discovery.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        setScreen(new LoginScreen());
    }

    @Override
    public void render() {
        // Global APIs update
        Scheduler.update();
        Sequence.updateAll();
        Event.update();

        super.render();
    }

    public static HGame get() {
        return instance;
    }

    public static void main(String[] args) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

        config.title = "H-Game";
        config.width = 720;
        config.height = 720;

        new LwjglApplication(new HGame(), config);
    }
}
