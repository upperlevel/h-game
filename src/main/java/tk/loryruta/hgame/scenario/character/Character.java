package tk.loryruta.hgame.scenario.character;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import lombok.Getter;
import lombok.Setter;
import tk.loryruta.hgame.HGame;
import tk.loryruta.hgame.scenario.Conversation;
import tk.loryruta.hgame.scenario.DystopianScenario;
import tk.loryruta.hgame.scenario.animation.Sequence;
import tk.loryruta.hgame.scenario.scheduler.Scheduler;

public class Character {
    public static final float WIDTH = 1.0f;
    public static final float HEIGHT = 1.5f; // Head.HEIGHT

    @Getter
    @Setter
    private String name;

    @Getter
    private float x, y;

    @Getter
    @Setter
    private boolean left;

    @Getter
    private Vector2 velocity = new Vector2();

    private Sprite sprite;
    private TextureRegion[][] regions;

    private int walkToTask = -1;
    private int walkTask = -1;
    private int backToIdle = -1;

    private int sayTask = -1;

    @Setter
    private Sequence onTalk;

    public Character(String name, String imagePath) {
        this.name = name;

        Texture texture = new Texture(Gdx.files.internal(imagePath));
        sprite = new Sprite(texture);
        sprite.setSize(WIDTH, HEIGHT);

        regions = SpriteExtractor.grid(texture, 5, 2);
        setFrame(0, 0);
    }

    public void setFrame(int x, int y) {
        sprite.setRegion(regions[x][y]);
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void walkTo(float x, float speed, Runnable reach) {
        if (this.x == x) {
            reach.run();
            return;
        }
        float absSpeed = Math.abs(speed);
        boolean left = this.x < x;
        walkToTask = Scheduler.start(() -> {
            move(left ? absSpeed : -absSpeed, 0);
            if ((left && this.x >= x) || (!left && this.x <= x)) {
                reach.run();
                Scheduler.cancel(walkToTask);
            }
        }, 1, true);
    }

    public void walkTo(Character who, float distance, float speed, Runnable reach) {
        if (who.x < x) {
            walkTo(who.x + WIDTH / 2.0f + distance, speed, reach);
        } else {
            walkTo(who.x - WIDTH / 2.0f - distance, speed, reach);
        }
    }

    public void say(String text, String audioPath, long duration) {
        if (sayTask != -1) {
            Scheduler.cancel(sayTask);
            sayTask = -1;
        }
        if (duration > 0) {
            sayTask = Scheduler.start(() -> {
                HGame.instance.getScenario().setRenderingSentence(this, null);
            }, duration);
        }
        Conversation.Sentence s = new Conversation.Sentence(text, audioPath);
        HGame.instance.getScenario().setRenderingSentence(this, s);
        if (s.getAudio() != null) {
            s.getAudio().play(1.0f);
        }
    }

    public void say(String text, String audioPath) {
        say(text, audioPath, -1);
    }

    public void setVelocity(float velocityX, float velocityY) {
        velocity.set(velocityX, velocityY);
    }

    public boolean intersect(Character other) {
        return (x >= other.x && x <= other.x + WIDTH) || (x + WIDTH >= other.x && x + WIDTH <= other.x + WIDTH);
    }

    public void talk(Character other) {
        left = other.x - x < 0;
        if (onTalk != null) {
            onTalk.play();
        }
    }

    public void move(float offsetX, float offsetY) {
        left = offsetX < 0;
        if (walkTask == -1) {
            walkTask = Scheduler.start(new Walking(), 100, true);
        }
        if (backToIdle != -1) {
            Scheduler.cancel(backToIdle);
        }
        backToIdle = Scheduler.start(() -> {
            Scheduler.cancel(walkTask);
            walkTask = -1;
            setFrame(0, 0);
            backToIdle = -1;
        }, 100);
        x += offsetX;
        y += offsetY;
    }

    public void update(DystopianScenario scenario, float delta) {
        velocity.y -= scenario.getGravity() * delta;
        x += velocity.x * delta;
        y += velocity.y * delta;

        float ground = scenario.getGroundHeight();
        if ( y < ground) {
            y = ground;
            velocity.y = 0;
        }
    }

    public void render() {
        if (left != sprite.isFlipX()) {
            sprite.flip(true, false);
        }
        sprite.setPosition(x, y);
        sprite.draw(HGame.instance.getBatch());
    }

    public class Walking implements Runnable {
        private int frame;
        private boolean backward;

        @Override
        public void run() {
            setFrame(frame, 1);
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
