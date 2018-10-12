package tk.loryruta.hgame.scenario.character;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import lombok.Getter;
import tk.loryruta.hgame.HGame;
import tk.loryruta.hgame.scenario.scheduler.Scheduler;

public class Body {
    public static final int WIDTH = 1;
    public static final int HEIGHT = 1;

    @Getter
    private final Character character;

    @Getter
    private Sprite sprite;

    private TextureRegion[] walkingAnimation;
    private int walkingTask;

    private TextureRegion idleAnimation;
    //private int idleFrame;
    //private int idleTask;

    private TextureRegion[] punchingAnimation;
    private int punchingTask;

    public Body(Character character, String path) {
        this.character = character;

        Texture texture = new Texture(Gdx.files.internal(path));
        walkingAnimation = SpriteExtractor.horizontal(texture, 3, 3, 0, 0, 3);
        walkingTask = -1;

        idleAnimation = SpriteExtractor.horizontal(texture, 3, 3, 1, 0, 1)[0];

        punchingAnimation = SpriteExtractor.horizontal(texture, 3, 3, 2, 0, 3);
        punchingTask = -1;

        sprite = new Sprite(texture);
        sprite.setSize(1, 1);
        setFrame(idleAnimation);
    }

    public void setWalking(int frame) {
        setFrame(walkingAnimation[frame]);
    }

    public void setIdle() {
        setFrame(idleAnimation);
    }

    public void setPunching(int frame) {
        setFrame(punchingAnimation[frame]);
    }

    public void setFrame(TextureRegion region) {
        sprite.setRegion(region);
    }

    public void onMove(float offsetX, float offsetY) {
        if (offsetX != 0) {
            if (walkingTask == -1) { // if the player has just started walking starts animation
                walkingTask = Scheduler.start(new Walking(), 100, true);
            }
        } else {
            if (walkingTask != -1) { // if the player has just stopped walking stops animation
                Scheduler.cancel(walkingTask);
                walkingTask = -1;
                setFrame(idleAnimation);
            }
        }
    }

    public void onPunch() {
        if (punchingTask != -1) {
            Scheduler.cancel(punchingTask);
            punchingTask = -1;
        }
        setFrame(punchingAnimation[0]);
        punchingTask = Scheduler.start(() -> {
            setFrame(punchingAnimation[1]);
            punchingTask = Scheduler.start(() -> {
                setFrame(idleAnimation);
                punchingTask = -1;
            }, 100);
        }, 100);
    }

    public void onRender() {
        if (character.isLeft() != getSprite().isFlipX()) {
            sprite.flip(true, false);
        }
        sprite.setPosition(character.getX(), character.getY());
        sprite.setSize(WIDTH, HEIGHT);
        sprite.draw(HGame.instance.getBatch());
    }

    public class Walking implements Runnable {
        private int frame;
        private boolean backward;

        @Override
        public void run() {
            setFrame(walkingAnimation[frame]);
            if (backward) {
                frame--;
            } else {
                frame++;
            }
            if (frame < 0) {
                frame = 0;
                backward = false;
            } else if (frame == 3) {
                frame = 2;
                backward = true;
            }
        }
    }
}
