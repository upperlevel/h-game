package tk.loryruta.hgame.scenario;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.Getter;
import tk.loryruta.hgame.HGame;
import tk.loryruta.hgame.scenario.animation.Sequence;
import tk.loryruta.hgame.scenario.animation.Sequence.Waiter;
import tk.loryruta.hgame.scenario.character.Character;
import tk.loryruta.hgame.scenario.character.Element;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.out;
import static tk.loryruta.hgame.scenario.animation.Sequence.Waiter.ENTER_KEY;

public class DystopianScenario {
    public static final float CONTROL_SENSITIVITY = 0.05f;
    public static final float JUMP_STRENGTH = 5.0f;

    @Getter
    private int width, height;

    @Getter
    private float groundHeight;

    @Getter
    private float gravity;

    private Texture background;

    @Getter
    private ConversationRenderer conversationRenderer;

    @Getter
    private Character actor;

    private Character googleDirector;
    private Character winston;

    private List<Character> extras = new ArrayList<>();
    private List<Element> elements = new ArrayList<>();

    private boolean frozen = false;

    public DystopianScenario(JsonObject json) {
        width = json.getAsJsonPrimitive("width").getAsInt();
        height = json.getAsJsonPrimitive("height").getAsInt();

        groundHeight = 0;
        gravity = json.getAsJsonPrimitive("gravity").getAsFloat();

        background = new Texture(Gdx.files.internal("images/dystopian_background.png"));

        actor = new Character("Ruta", "images/dystopian_john.png");
        actor.setPosition(0.0f, 0);

        conversationRenderer = new ConversationRenderer();

        elements.add(new Element(35.0f, 0f, 1f, 1f, "images/dystopian_camera.png"));

        spawn(googleDirector = new GoogleDirector());
        spawn(new John());
        spawn(new Steve());
        spawn(new Justin());
        spawn(new Lara());
        spawn(new Cassandra());
        spawn(winston = new Winston());
    }

    public void spawn(Character character) {
        extras.add(character);
    }

    public void onJoin() {
        new Sequence()
                .append(handle -> {
                    frozen = true;
                    googleDirector.setLeft(false);
                    handle.next(Waiter.sleep(3000));
                })
                .append(handle -> {
                    googleDirector.setLeft(true);
                    handle.next(Waiter.sleep(1000));
                })
                .append(handle -> {
                    googleDirector.setVelocity(0f, 2.0f);
                    googleDirector.say(
                            "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAH!!!",
                            "dgd_aaaaaaaaa.mp3"
                    );
                    handle.next(Waiter.sleep(3000));
                })
                .append(handle -> {
                    googleDirector.walkTo(actor, 0.25f, 0.05f, () -> {
                        handle.next(Waiter.sleep(1000));
                    });
                })
                .append(handle -> {
                    googleDirector.setFrame(2, 0);
                    googleDirector.say(
                            "WHO ARE YOU!?",
                            "dgd_who_are_you.mp3"
                    );
                    handle.next(ENTER_KEY);
                })
                .append(handle -> {
                    googleDirector.setFrame(3, 0);
                    googleDirector.say(
                            "You just appeared from nowhere!",
                            "dgd_you_appeared_suddenly.mp3"
                    );
                    handle.next(ENTER_KEY);
                })
                .append(handle -> {
                    googleDirector.setFrame(4, 0);
                    googleDirector.say(
                            "I'll call the police!",
                            "dgd_call_the_police.mp3"
                    );
                    handle.next(ENTER_KEY);
                })
                // Me: Ehm, I'm just playing...
                .append(handle -> {
                    googleDirector.setFrame(3, 0);
                    googleDirector.say(
                            "Oh ok, my fault. Anyway, I'm the director of Google and a member of the BDCO council.",
                            "dgd_my_fault.mp3"
                    );
                    handle.next(ENTER_KEY);
                })
                // Me: BD what?
                .append(handle -> {
                    googleDirector.setFrame(0, 0);
                    googleDirector.say(
                            "BDCO means Big-Data Companies Organization and is what owns this world.",
                            "dgd_bdco.mp3"
                    );
                    handle.next(ENTER_KEY);
                })
                .append(handle -> {
                    googleDirector.setFrame(0, 0);
                    googleDirector.say(
                            "Countries, governments, are all gone years and years ago. Now we are the only owners.",
                            "dgd_only_owners.mp3"
                    );
                    handle.next(ENTER_KEY);
                })
                .append(handle -> {
                    googleDirector.setFrame(2, 0);
                    googleDirector.say(
                            "What you have to (ehm, must) believe in.",
                            "dgd_what_you_have_to.mp3"
                    );
                    handle.next(ENTER_KEY);
                })
                .append(handle -> {
                    googleDirector.setFrame(0, 0);
                    googleDirector.say(
                            "Have a nice day.",
                            "dgd_have_a_nice_day.mp3"
                    );
                    handle.next(Waiter.sleep(1000));
                })
                .append(handle -> {
                    frozen = false;
                });
                //.play();
    }

    public Character getListener() {
        for (Character extra : extras) {
            if (actor.intersect(extra)) {
                return extra;
            }
        }
        return null;
    }

    public void setRenderingSentence(Character speaker, Conversation.Sentence sentence) {
        conversationRenderer.setSentence(speaker, sentence);
    }

    private boolean winstonAnimation = false;

    public void update(float delta) {
        if (!frozen) {
            // move
            if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                actor.move(-CONTROL_SENSITIVITY, 0);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                actor.move(CONTROL_SENSITIVITY, 0);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                actor.move(0, -CONTROL_SENSITIVITY);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.W)) {
                actor.move(0, CONTROL_SENSITIVITY);
            }
            // chat
            if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
                Character listener = getListener();
                if (listener != null) {
                    listener.talk(actor);
                }
            }
        }

        /* Debug */
        if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
            out.println("Actor information:");
            out.println("Velocity: " + actor.getVelocity());
            out.println("Position: (" + actor.getX() + " " + actor.getY() + ")");
        }

        /* Update */
        actor.update(this, delta);
        extras.forEach(extra -> extra.update(this, delta));

        if (!winstonAnimation && actor.getX() >= 60.0f) {
            new Sequence()
                    .append(handle -> {
                        frozen = true;
                        handle.next(Waiter.sleep(5000));
                    })
                    .append(handle -> {
                        winston.walkTo(actor, 0.25f, 0.025f, () -> {
                            handle.next(Waiter.sleep(1000));
                        });
                    })
                    .append(handle -> {
                        winston.say(
                                "Have you seen this world?",
                                "dystopian_winston_1.mp3"
                        );
                        handle.next(Waiter.ENTER_KEY);
                    })
                    .append(handle -> {
                        winston.walkTo(62.0f, 0.025f, () -> {
                            handle.next(Waiter.sleep(100));
                        });

                    })
                    .play();
            winstonAnimation = true;
        }
    }

    public void render() {
        OrthographicCamera camera = HGame.instance.getCamera();
        SpriteBatch batch = HGame.instance.getBatch();

        camera.setToOrtho(false, Gdx.graphics.getWidth() / (float) Gdx.graphics.getHeight() * height, height);
        camera.position.x = actor.getX() + Character.WIDTH / 2f;
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        // World
        batch.begin();

        elements.forEach(Element::render);
        extras.forEach(Character::render);

        actor.render();

        batch.end();


        // GUI
        // todo remove conversation.render();
        conversationRenderer.render();
    }

    public class GoogleDirector extends Character {
        public GoogleDirector() {
            super("Google Director", "images/dystopian_google_director.png");
            setPosition(2.0f, 0f);
            setOnTalk(new Sequence()
                    .append(handle -> {
                        frozen = true;
                        say(
                                "Away from my balls!",
                                "dystopian_google_1.mp3"
                        );
                        handle.next(ENTER_KEY);
                    })
                    .append(handle -> {
                        frozen = false;
                    })
            );
        }
    }

    public class John extends Character {
        public John() {
            super("John", "images/dystopian_john.png");
            setPosition(12.0f, 0f);
            setOnTalk(new Sequence()
                    .append(handle -> {
                        frozen = true;
                        setFrame(3, 0);
                        say(
                                "Google's main duty is to prevent criminality, the last week it suddenly arrested two people that were plotting a terrorist attack.",
                                "dystopian_john_1.mp3"
                        );
                        handle.next(ENTER_KEY);
                    })
                    .append(handle -> {
                        setFrame(1, 0);
                        say(
                                "I can't imagine how people could leave without this level of safety.",
                                "dystopian_john_2.mp3"
                        );
                        handle.next(ENTER_KEY);
                    })
                    .append(handle -> {
                        setFrame(0, 0);
                        frozen = false;
                    })
            );
        }
    }

    public class Steve extends Character {
        public Steve() {
            super("Steve", "images/dystopian_steve.png");
            setPosition(22.0f, 0f);
            setOnTalk(new Sequence()
                    .append(handle -> {
                        frozen = true;
                        setFrame(3, 0);
                        say(
                                "Apple suggests us what to wear daily.",
                                "dystopian_steve_1.mp3"
                        );
                        handle.next(ENTER_KEY);
                    })
                    .append(handle -> {
                        setFrame(1, 0);
                        say(
                                "Today they suggested me to wear this cool yellow shirt!",
                                "dystopian_steve_2.mp3"
                        );
                        handle.next(ENTER_KEY);
                    })
                    .append(handle -> {
                        setFrame(3, 0);
                        say(
                                "This activity can be done thanks to Microsoft that manages the data coming from our phones cameras.",
                                "dystopian_steve_3.mp3"
                        );
                        handle.next(ENTER_KEY);
                    })
                    .append(handle -> {
                        say(
                                "This activity can be done thanks to Microsoft that manages the data coming from our phones and public cameras.",
                                "dystopian_steve_4.mp3"
                        );
                        handle.next(ENTER_KEY);
                    })
                    .append(handle -> {
                        // todo clear chat
                        setFrame(0, 0);
                        frozen = false;
                    })
            );
        }
    }

    public class Justin extends Character {
        public Justin() {
            super("Justin", "images/dystopian_justin.png");
            setPosition(32.0f, 0f);

            setLeft(false);
            setFrame(3, 0);

            setOnTalk(new Sequence()
                    .append(handle -> {
                        frozen = true;
                        setFrame(2, 0);
                        setLeft(!isLeft());
                        handle.next(Waiter.sleep(500));
                    })
                    .append(handle -> {
                        setFrame(4, 0);
                        handle.next(Waiter.sleep(500));
                    })
                    .append(handle -> {
                        setFrame(3, 0);
                        handle.next(Waiter.sleep(500));
                    })
                    .append(handle -> {
                        setLeft(!isLeft());
                        setFrame(2, 0);
                        say(
                                "What a luck! Nowadays there's no need of daily posting our pictures, The World does it for us!",
                                "dystopian_justin_1.mp3"
                        );
                        handle.next(Waiter.ENTER_KEY);
                    })
                    .append(handle -> {
                        setFrame(0, 0);
                        say(
                                "Just pose in front of those public cameras and smile to the BDCO!",
                                "dystopian_justin_2.mp3"
                        );
                        handle.next(Waiter.ENTER_KEY);
                    })
                    .append(handle -> {
                        setFrame(3, 0);
                        setLeft(!isLeft());
                        frozen = false;
                    })
            );
        }
    }

    public class Lara extends Character {
        public Lara() {
            super("Lara", "images/dystopian_lara.png");
            setPosition(42.0f, 0f);
            setOnTalk(new Sequence()
                    .append(handle -> {
                        frozen = true;
                        say(
                                "Last week my son has been kidnapped by a fool.",
                                "dystopian_lara_1.mp3"
                        );
                        handle.next(Waiter.ENTER_KEY);
                    })
                    .append(handle -> {
                        say(
                                "There's no need to say that the guilty has been caught some minute after.",
                                "dystopian_lara_2.mp3"
                        );
                        handle.next(Waiter.ENTER_KEY);
                    })
                    .append(handle -> {
                        setFrame(2, 0);
                        say(
                                "I LOVE BDCO!",
                                "dystopian_lara_3.mp3"
                        );
                        handle.next(Waiter.sleep(1000));
                    })
                    .append(handle -> {
                        setFrame(0, 0);
                        frozen = false;
                    })
            );
        }
    }

    public class Cassandra extends Character {
        public Cassandra() {
            super("Lara", "images/dystopian_cassandra.png");
            setPosition(52.0f, 0f);
            setOnTalk(new Sequence()
                    .append(handle -> {
                        frozen = true;
                        say(
                                "BDCO starts spying you from when you are 8 years old.",
                                "dystopian_cassandra_1.mp3"
                        );
                        handle.next(Waiter.ENTER_KEY);
                    })
                    .append(handle -> {
                        setFrame(3, 0);
                        say(
                                "I'm fighting to make my 5 years old son be spied and protected.",
                                "dystopian_cassandra_2.mp3"
                        );
                        frozen = false;
                    })
            );
        }
    }

    public class Winston extends Character {
        public Winston() {
            super("Winston", "images/dystopian_winston.png");
            setPosition(62.0f, 0f);

            setLeft(true);
        }
    }

    public static DystopianScenario from(String path) {
        try {
            return new DystopianScenario(new Gson().fromJson(new FileReader(path), JsonObject.class));
        } catch (FileNotFoundException e) {
            throw new IllegalStateException("Can't find file: " + path, e);
        }
    }
}
