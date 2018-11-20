package xyz.upperlevel.hgame.scenario;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import xyz.upperlevel.hgame.scenario.character.Actor;
import xyz.upperlevel.hgame.scenario.scheduler.Task;

public class Conversation {
    public static final float STAY = 20;

    private static final BitmapFont font;

    static {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Lato.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 32;
        parameter.borderWidth = 2;
        parameter.borderColor = Color.BLACK;

        font = generator.generateFont(parameter);
        generator.dispose();
    }

    private static final Stage stage = new Stage(new ScreenViewport());
    private static Label nameLabel;
    private static Label textLabel;

    static {
        Table table = new Table();
        table.setFillParent(true);
        table.pad(10);
        table.align(Align.top | Align.left);

        nameLabel = new Label("", new Label.LabelStyle(font, Color.RED));
        nameLabel.setWrap(false);

        textLabel = new Label("", new Label.LabelStyle(font, Color.YELLOW));
        textLabel.setWrap(true);

        table.add(nameLabel)
                .align(Align.left)
                .growX();
        table.row();
        table.add(textLabel)
                .align(Align.left)
                .growX();

        table.pack();
        stage.addActor(table);
    }

    private static Sound playing = null; // the last sound played

    private static final Task delay = new Task() {
        @Override
        public void run() {
            hide();
        }
    };

    public static void create(String name, String text) {
        nameLabel.setText(name);
        textLabel.setText(text);
    }

    public static void hide() {
        if (playing != null) {
            playing.stop();
            playing = null;
        }

        // TODO: rude way to hide the conversation
        nameLabel.setText("");
        textLabel.setText("");
    }

    public static void show(String name, String text, String audio) {
        if (playing != null) {
            playing.stop();
        }

        if (!delay.isCanceled()) {
            delay.cancel();
        }
        create(name, text);
        delay.delay((long) STAY * 1000);

        if (audio != null) { // todo: load audio globally
            try {
                playing = Gdx.audio.newSound(Gdx.files.internal("resources/audio/" + audio));
                playing.play(100);
            } catch (Exception e) {
                System.err.println("[WARNING] Audio file not found: " + audio);
            }
        }
    }

    public static void show(String name, String text) {
        show(name, text, null);
    }

    public static void show(Actor actor, String text, String audio) {
        show(actor.getCharacter().getName() + ":", text, audio);
    }

    public static void show(Actor actor, String text) {
        show(actor, text, null);
    }

    public static void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    public static void render() {
        stage.act();
        stage.draw();
    }

    public static void dispose() {
        stage.dispose();
        font.dispose();
    }

    private Conversation() {
    }
}
