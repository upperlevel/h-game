package xyz.upperlevel.hgame;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import lombok.Getter;
import org.apache.logging.log4j.core.config.Configurator;
import xyz.upperlevel.hgame.network.discovery.UdpDiscovery;
import xyz.upperlevel.hgame.scenario.GameScreen;
import xyz.upperlevel.hgame.scenario.LoginScreen;
import xyz.upperlevel.hgame.scenario.MatchMakingScreen;

import java.io.IOException;

public class HGame extends Game {
    @Getter
    private UdpDiscovery discovery;

    // Screens
    private LoginScreen loginScreen;
    private MatchMakingScreen matchMakingScreen;
    private GameScreen mainScreen;

    @Override
    public void create() {
        Configurator.initialize("config", null, Gdx.files.internal("log4j2.xml").path());

        discovery = new UdpDiscovery();

        try {
            discovery.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        matchMakingScreen = new MatchMakingScreen(discovery, (oppIp, oppName) -> {
            // TODO: start the connection and the game
        });

        loginScreen = new LoginScreen(name -> {
            matchMakingScreen.setName(name);
            setScreen(matchMakingScreen);
        });

        mainScreen = new GameScreen();

        setScreen(mainScreen);
    }

    public static void main(String[] args) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

        config.title = "H-Game";
        config.width = 720;
        config.height = 720;

        new LwjglApplication(new HGame(), config);
    }
}
