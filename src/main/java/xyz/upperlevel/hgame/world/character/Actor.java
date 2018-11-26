package xyz.upperlevel.hgame.world.character;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import lombok.Getter;
import lombok.Setter;
import xyz.upperlevel.hgame.input.EntityInput;
import xyz.upperlevel.hgame.input.StandardEntityInput;
import xyz.upperlevel.hgame.world.Conversation;
import xyz.upperlevel.hgame.world.World;
import xyz.upperlevel.hgame.world.WorldRenderer;
import xyz.upperlevel.hgame.world.sequence.Sequence;
import xyz.upperlevel.hgame.world.sequence.Trigger;
import xyz.upperlevel.hgame.world.scheduler.Scheduler;

public class Actor {
    public static final float WIDTH = 2.0f;
    public static final float HEIGHT = 2.0f;

    @Getter
    private final int id;

    @Getter
    private Character character;

    @Getter
    public float x, y;

    @Getter
    @Setter
    private boolean left;

    @Getter
    @Setter
    private Vector2 velocity = new Vector2();

    @Getter
    private boolean touchingGround = false;

    private Sprite sprite;
    private TextureRegion[][] regions;

    public boolean rigidBody = true;
    public boolean noClip = false;

    private int walkToTask = -1;
    private int walkTask = -1;
    private int backToIdle = -1;

    private int sayTask = -1;

    @Getter
    @Setter
    private EntityInput input = StandardEntityInput.create(this);

    private Sequence animation = null;

    public Actor(int id, Character character) {
        this.id = id;
        this.character = character;

        Texture texture = new Texture(Gdx.files.internal("images/" + character.getTexturePath()));

        sprite = new Sprite(texture);
        sprite.setSize(WIDTH, HEIGHT);

        regions = SpriteExtractor.grid(texture, 9, 4);
        setFrame(0, 0);
    }

    /**
     * Allows to play the given animation with the security that the
     * previous animation is stopped. An {@link Actor} is supposed
     * to have one animation running per time.
     */
    public void animate(Sequence animation) {
        Sequence old = this.animation;
        if (old != null) {
            old.dismiss();
        }
        this.animation = animation;
        animation.play();
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
            move(left ? absSpeed : -absSpeed);
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
    public Trigger walkTo(Actor who, float distance, float speed, Runnable reach) {
        if (who.x < x) {
            return walkTo(who.x + WIDTH / 2.0f + distance, speed, reach);
        } else {
            return walkTo(who.x - WIDTH / 2.0f - distance, speed, reach);
        }
    }

    @Deprecated
    public Trigger walkTo(Actor who, float speed, Runnable reach) {
        return walkTo(who, 0.5f, speed, reach);
    }

    public Trigger walkTo(Actor who, float speed) {
        return walkTo(who, speed, null);
    }

    public void say(String text, String audio, long duration) {
        if (sayTask != -1) {
            Scheduler.cancel(sayTask);
            sayTask = -1;
        }
        if (duration > 0) {
            sayTask = Scheduler.start(() -> {
                // GameScreen.instance.getScenario().setRenderingSentence(this, null);
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

    public boolean intersect(Actor other) {
        return (x >= other.x && x <= other.x + WIDTH) || (x + WIDTH >= other.x && x + WIDTH <= other.x + WIDTH);
    }

    public void move(float offsetX) {
        left = offsetX < 0;
        if (walkTask == -1) {
            // The player is moving and the walking task wasn't started.
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
    }

    public void jump(float velocity) {
        setVelocity(0, velocity);
    }

    public void attack() {
        setFrame(2, 0);
        // TODO delay to remove
    }

    public void specialAttack() {
        // By default, special attack is implemented as a normal attack.
        // The Character should override the Actor class in order to implement its own special attack.
        attack();
    }

    public void update(World world) {
        float delta = Gdx.graphics.getDeltaTime();

        if (rigidBody) {
            velocity.y -= world.gravity * delta;
        }
        x += velocity.x * delta;
        y += velocity.y * delta;

        if (!noClip && y < world.groundHeight) {
            y = world.groundHeight;
            velocity.y = 0;
            touchingGround = true;
        } else {
            touchingGround = false;
        }
    }

    public void render(WorldRenderer renderer) {
        if (left != sprite.isFlipX()) {
            sprite.flip(true, false);
        }
        sprite.setPosition(x, y);
        sprite.draw(renderer.getSpriteBatch());
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
