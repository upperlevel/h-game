package tk.loryruta.hgame.scenario;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import tk.loryruta.hgame.scenario.character.Character;

public class ConversationRenderer {
    private Stage stage;
    private Table table;

    private BitmapFont font;

    private Character speaker;

    private Image headImage;
    private Label nameLabel;
    private Label textLabel;

    private Cell<Label> a;

    public ConversationRenderer() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("resources/Lato.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 32;
        parameter.borderWidth = 2;
        parameter.borderColor = Color.BLACK;

        font = generator.generateFont(parameter);
        generator.dispose();

        // --
        stage = new Stage();
        stage.setViewport(new ScreenViewport());

        Gdx.input.setInputProcessor(stage);

        table = new Table();
        table.setFillParent(true);
        table.top().left();

        headImage = new Image();
        table.add(headImage).size(128f);


        Table rightTable = new Table();

        nameLabel = new Label("", new Label.LabelStyle(font, Color.RED));
        nameLabel.setWrap(true);
        rightTable.add(nameLabel);

        rightTable.row();

        textLabel = new Label("", new Label.LabelStyle(font, Color.YELLOW));
        textLabel.setWrap(true);
        rightTable.add(textLabel);

        table.add(rightTable);

        stage.addActor(table);
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    public void setSentence(Character speaker, Conversation.Sentence sentence) {
        this.speaker = speaker;
        headImage.setDrawable(new TextureRegionDrawable(speaker.getHead().getSprite()));

        nameLabel.setText(speaker.getName() + ":");
        textLabel.setText(sentence.getText());
    }

    public void render() {
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    public void dispose() {
        stage.dispose();
    }
}
