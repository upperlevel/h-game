package xyz.upperlevel.hgame.scenario.character;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import lombok.Getter;
import lombok.Setter;
import xyz.upperlevel.hgame.HGame;
import xyz.upperlevel.hgame.scenario.Conversation;
import xyz.upperlevel.hgame.scenario.Scenario;
import xyz.upperlevel.hgame.scenario.animation.Sequence;
import xyz.upperlevel.hgame.scenario.animation.Trigger;
import xyz.upperlevel.hgame.scenario.scheduler.Scheduler;

public class Human {
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

    public boolean rigidBody = true;
    public boolean noClip = false;

    private int walkToTask = -1;
    private int walkTask = -1;
    private int backToIdle = -1;

    private int sayTask = -1;

    @Setter
    private Sequence onTalk;

    public Human(String name, String imagePath) {
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

    @Deprecated
    public Trigger walkTo(float x, float speed, Runnable reach) {
        if (this.x == x) {
            reach.run();
            return Trigger.NONE;
        }
        float absSpeed = Math.abs(speed);
        boolean left = this.x < x;
        Trigger endWhen = () -> (left && this.x >= x) || (!left && this.x <= x);
        walkToTask = Scheduler.start(() -> {
            move(left ? absSpeed : -absSpeed, 0);
            if (endWhen.get()) {
                if (reach != null) {
                    reach.run();
                }
                Scheduler.cancel(walkToTask);
            }
        }, 1, true);
        return endWhen;
    }

    public Trigger walkTo(float x, float speed) {
        return walkTo(x, speed, null);
    }

    @Deprecated
    public Trigger walkTo(Human who, float distance, float speed, Runnable reach) {
        if (who.x < x) {
            return walkTo(who.x + WIDTH / 2.0f + distance, speed, reach);
        } else {
            return walkTo(who.x - WIDTH / 2.0f - distance, speed, reach);
        }
    }

    @Deprecated
    public Trigger walkTo(Human who, float speed, Runnable reach) {
        return walkTo(who, 0.5f, speed, reach);
    }

    public Trigger walkTo(Human who, float speed) {
        return walkTo(who, speed, null);
    }

    public void say(String text, String audio, long duration) {
        if (sayTask != -1) {
            Scheduler.cancel(sayTask);
            sayTask = -1;
        }
        if (duration > 0) {
            sayTask = Scheduler.start(() -> {
                // HGame.instance.getScenario().setRenderingSentence(this, null);
            }, duration);
        }
        Conversation.show(this, text, audio);
    }

    public void say(String text, String audioPath) {
        say(text, audioPath, -1);
    }

    public void setVelocity(float velocityX, float velocityY) {
        velocity.set(velocityX, velocityY);
    }

    public boolean intersect(Human other) {
        return (x >= other.x && x <= other.x + WIDTH) || (x + WIDTH >= other.x && x + WIDTH <= other.x + WIDTH);
    }

    public void talk(Human main) {
        left = main.x - x < 0;
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

    public void update(Scenario scenario) {
        float delta = Gdx.graphics.getDeltaTime();

        if (rigidBody) {
            velocity.y -= scenario.gravity * delta;
        }
        x += velocity.x * delta;
        y += velocity.y * delta;

        if (!noClip && y < scenario.groundHeight) {
            y = scenario.groundHeight;
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

    @Override
    public String toString() {
        return name;
    }
}
