package tk.loryruta.hgame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.Getter;
import tk.loryruta.hgame.scenario.Scenario;
import tk.loryruta.hgame.scenario.scheduler.Scheduler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.logging.*;

public class HGame extends ApplicationAdapter {
    public static Logger logger;

    public static Gson gson = new Gson();

    private static void initLogger() {
        logger = Logger.getLogger("hgame");
        logger.setLevel(Level.ALL); // todo just to debug
        logger.setUseParentHandlers(false);

        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new Formatter() {
            @Override
            public String format(LogRecord record) {
                return "[" + record.getLevel() + "] " + record.getMessage() + "\n";
            }
        });
        logger.addHandler(handler);
    }

    public static void main(String[] args) {
        initLogger();

        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

        config.title = "H-Game";
        config.width = 720;
        config.height = 720;

        new LwjglApplication(new HGame(), config);
    }

    public static HGame instance;

    @Getter
    private OrthographicCamera camera;

    @Getter
    private SpriteBatch batch;

    @Getter
    private ShapeRenderer shapeRenderer;

    @Getter
    private Scenario scenario;
    private long lastUpdateTime;

    @Override
    public void create() {
        instance = this;

        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        scenario = Scenario.from("resources/scenario.json");

        camera = new OrthographicCamera();
    }

    @Override
    public void resize(int w, int h) {
        scenario.getConversationRenderer().resize(w, h);
    }

    @Override
    public void render() {
        int width = Gdx.graphics.getWidth();
        int height = Gdx.graphics.getHeight();

        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update
        long currTime = System.currentTimeMillis();
        if (lastUpdateTime < 0) {
            lastUpdateTime = currTime; // default init
        }

        float delta = (currTime - lastUpdateTime) / 1000.0f;
        lastUpdateTime = currTime;

        scenario.update(delta);
        Scheduler.update();

        // Render
        scenario.render();
    }

    @Override
    public void dispose() {
        batch.dispose();
    }

    public static JsonObject json(String path) {
        try {
            return HGame.gson.fromJson(new FileReader(new File("resources", path)), JsonObject.class);
        } catch (FileNotFoundException e) {
            throw new IllegalStateException("Can't read file at: " + path);
        }
    }
}
