package xyz.upperlevel.hgame.scenario;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import xyz.upperlevel.hgame.HGame;
import xyz.upperlevel.hgame.scenario.animation.Sequence;
import xyz.upperlevel.hgame.scenario.animation.Trigger;
import xyz.upperlevel.hgame.scenario.character.Element;
import xyz.upperlevel.hgame.scenario.character.Human;

import java.util.ArrayList;
import java.util.List;

public class DystopianScene extends Scenario {
    private Human gdirector;
    private Human winston;

    private List<Element> elements = new ArrayList<>();

    public DystopianScene() {
        groundColor = Color.WHITE;

        elements.add(new Element(33.5f, groundHeight + 1, 1, 1, "images/camera.png"));

        player.setPosition(0, 0);

        spawn(gdirector = new GoogleDirector());
        spawn(new John());
        spawn(new Steve());
        spawn(new Justin());
        spawn(new Lara());
        spawn(new Cassandra());
        spawn(winston = new Winston());

        player.say(
                "WHAT!? Am I in a dystopian world?",
                "dystopian_player_4.mp3"
        );

        Event.watch(Event.greaterX(player, 60), () -> {
            new Sequence()
                    .append(handle -> {
                        freeze(true);
                        handle.next(Trigger.sleep(2500));
                    })
                    .append(handle -> {
                        winston.setFrame(2, 0);
                        winston.say(
                                "Have you seen this world?",
                                "dystopian_winston_1.mp3"
                        );
                        handle.next(Trigger.ENTER_KEY);
                    })
                    .append(handle ->
                            winston.walkTo(player, 0.25f, 0.025f, () ->
                                    handle.next(Trigger.sleep(1000))))
                    .append(handle -> {
                        winston.setFrame(3, 0);
                        winston.say(
                                "How long and how deep will our privacy be threatened?",
                                "dystopian_winston_2.mp3"
                        );
                        handle.next(Trigger.ENTER_KEY);
                    })
                    .append(handle -> {
                        winston.say(
                                "BDCO holds all our information: from the amount of money in your back account to the number of teeth in your mouth.",
                                "dystopian_winston_3.mp3"
                        );
                        handle.next(Trigger.ENTER_KEY);
                    })
                    .append(handle -> {
                        winston.say(
                                "Is it fair?",
                                "dystopian_winston_4.mp3"
                        );
                        handle.next(Trigger.ENTER_KEY);
                    })
                    .append(handle -> {
                        winston.say(
                                "We don't know anything on what BDCO does* with our data, but most of the people seems blinded from " +
                                        "the services that Internet offers.",
                                "dystopian_winston_5.mp3"
                        );
                        handle.next(Trigger.ENTER_KEY);
                    })
                    .append(handle -> {
                        winston.setFrame(2, 0);
                        winston.say(
                                "If They want you to lose your job because you only wear yellow shirts, they'll do it and you'll never know.",
                                "dystopian_winston_6.mp3"
                        );
                        handle.next(Trigger.ENTER_KEY);
                    })
                    .append(handle -> {
                        winston.setFrame(3, 0);
                        winston.say(
                                "They control us and we have too much ignorance on our side.",
                                "dystopian_winston_7.mp3"
                        );
                        handle.next(Trigger.ENTER_KEY);
                    })
                    .append(handle -> {
                        winston.setFrame(2, 0);
                        winston.say(
                                "We MUST trust them and to achieve that, we MUST know ALL OF THEIR WORK AS THEY KNOW ALL OF US.",
                                "dystopian_winston_8.mp3"
                        );
                        handle.next(Trigger.ENTER_KEY);
                    })
                    .append(handle ->
                            winston.walkTo(62.0f, 0.025f, () ->
                                    handle.next(Trigger.sleep(2500))))
                    .append(handle -> {
                        winston.setFrame(3, 0);
                        winston.say(
                                "Now I'm here, hopeless, staring at the future I want.",
                                "dystopian_winston_9.mp3"
                        );
                        handle.next(Trigger.ENTER_KEY);
                    })
                    .append(handle -> freeze(false))
                    .play();
        });

        Event.watch(Event.greaterX(player, 70), () -> {
            player.rigidBody = false;
            player.setVelocity(0, 1);
            new Sequence()
                    .append(handle -> {
                        player.say(
                                "Winston... WHAT THE HELL IS THAT!? HEEEELP WHERE I'M GOING!?",
                                "dystopian_player_3.mp3"
                        );
                        handle.next(Trigger.ENTER_KEY);
                    })
                    .append(handle -> {
                        HGame.instance.getStoryline().scenario = new UtopianScene(); // change to the utopian scenario
                        handle.next();
                    })
                    .play();
        });

        // Player intro
        Event.watch(Event.greaterX(player, 5.0f), () -> {
            freeze(true);
            new Sequence()
                    .append(handle -> {
                        gdirector.say(
                                "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAH!!!",
                                "dystopian_gdirector_1.mp3"
                        );
                        gdirector.walkTo(player, 0.5f, 0.05f, () -> {
                            gdirector.setVelocity(0.0f, 5.0f);
                            handle.next(Trigger.ENTER_KEY);
                        });
                    })
                    .append(handle -> {
                        gdirector.setFrame(2, 0);
                        gdirector.say(
                                "WHO ARE YOU!?",
                                "dystopian_gdirector_2.mp3"
                        );
                        handle.next(Trigger.ENTER_KEY);
                    })
                    .append(handle -> {
                        gdirector.setFrame(3, 0);
                        gdirector.say(
                                "You just appeared suddenly from nowhere!",
                                "dystopian_gdirector_3.mp3"
                        );
                        handle.next(Trigger.ENTER_KEY);
                    })
                    .append(handle -> {
                        gdirector.setFrame(4, 0);
                        gdirector.say(
                                "I'll call the police!",
                                "dystopian_gdirector_4.mp3"
                        );
                        handle.next(Trigger.ENTER_KEY);
                    })
                    .append(handle -> {
                        player.setFrame(3, 0);

                        player.say(
                                "I don't know I was at home and I got trapped in this game!",
                                "dystopian_player_1.mp3"
                        );
                        handle.next(Trigger.ENTER_KEY);
                    })
                    .append(handle -> {
                        player.setFrame(0, 0);

                        gdirector.setFrame(3, 0);
                        gdirector.say(
                                "OH NICE! You're a new player... anyway, I'm the director of Google and a member of the BDCO council.",
                                "dystopian_gdirector_5.mp3"
                        );
                        handle.next(Trigger.ENTER_KEY);
                    })
                    .append(handle -> {
                        gdirector.setFrame(0, 0);

                        player.say(
                                "BD what...?",
                                "dystopian_player_2.mp3"
                        );
                        handle.next(Trigger.ENTER_KEY);
                    })
                    .append(handle -> {
                        gdirector.say(
                                "BDCO means Big-Data Companies Organization and is what owns this world.",
                                "dystopian_gdirector_6.mp3"
                        );
                        handle.next(Trigger.ENTER_KEY);
                    })
                    .append(handle -> {
                        gdirector.setFrame(0, 0);
                        gdirector.say(
                                "Countries, governments, are all gone years and years ago. Now we are the only owners.",
                                "dystopian_gdirector_7.mp3"
                        );
                        handle.next(Trigger.ENTER_KEY);
                    })
                    .append(handle -> {
                        gdirector.setFrame(2, 0);
                        gdirector.say(
                                "What you have to (ehm, must) believe in.",
                                "dystopian_gdirector_8.mp3"
                        );
                        handle.next(Trigger.ENTER_KEY);
                    })
                    .append(handle -> {
                        gdirector.setFrame(0, 0);
                        gdirector.say(
                                "Have a nice day.",
                                "dystopian_gdirector_9.mp3"
                        );
                        handle.next(Trigger.sleep(1000));
                    })
                    .append(handle -> freeze(false))
                    .play();
        });
    }

    @Override
    public void render() {
        Color bg = Color.DARK_GRAY;
        Gdx.gl.glClearColor(bg.r, bg.g, bg.b, bg.a);
        super.render();
    }

    @Override
    public void onRender() {
        elements.forEach(Element::render);
    }

    public class GoogleDirector extends Human {
        public GoogleDirector() {
            super("Google Director", "images/google_director.png");
            setPosition(10.0f, 0f);
            setOnTalk(new Sequence()
                    .append(handle -> {
                        freeze(true);
                        say(
                                "Away from my balls!",
                                "dystopian_google_chat_1.mp3"
                        );
                        handle.next(Trigger.ENTER_KEY);
                    })
                    .append(handle -> {
                        freeze(false);
                    })
            );
        }
    }

    public class John extends Human {
        public John() {
            super("John", "images/dystopian_john.png");
            setPosition(12.0f, 0f);
            setOnTalk(new Sequence()
                    .append(handle -> {
                        freeze(true);
                        setFrame(3, 0);
                        say(
                                "Google's main duty is to prevent criminality, the last week it suddenly arrested two people that were plotting a terrorist attack.",
                                "dystopian_john_1.mp3"
                        );
                        handle.next(Trigger.ENTER_KEY);
                    })
                    .append(handle -> {
                        say(
                                "I can't imagine how people could live without this level of safety.",
                                "dystopian_john_2.mp3"
                        );
                        handle.next(Trigger.ENTER_KEY);
                    })
                    .append(handle -> {
                        setFrame(4, 0);
                        say(
                                "Please BDCO, spy us more to save us better from criminality!",
                                "dystopian_john_3.mp3"
                        );
                        handle.next(Trigger.ENTER_KEY);
                    })
                    .append(handle -> {
                        setFrame(0, 0);
                        freeze(false);
                    })
            );
        }
    }

    public class Steve extends Human {
        public Steve() {
            super("Steve", "images/dystopian_steve.png");
            setPosition(22.0f, 0f);
            setOnTalk(new Sequence()
                    .append(handle -> {
                        freeze(true);
                        setFrame(3, 0);
                        say(
                                "Apple suggests us what to wear daily. " +
                                        "Today they told me I was more likely to get a new job with this yellow shirt on!",
                                "dystopian_steve_1.mp3"
                        );
                        handle.next(Trigger.ENTER_KEY);
                    })
                    .append(handle -> {
                        say(
                                "That can be done thanks to Microsoft that manages the data coming from our phones camera.",
                                "dystopian_steve_2.mp3"
                        );
                        handle.next(Trigger.ENTER_KEY);
                    })
                    .append(handle -> {
                        say(
                                "It's beautiful isn't it?",
                                "dystopian_steve_3.mp3"
                        );
                        handle.next(Trigger.ENTER_KEY);
                    })
                    .append(handle -> {
                        say(
                                "I can't even remember how to dress on my own!",
                                "dystopian_steve_4.mp3"
                        );
                        handle.next(Trigger.ENTER_KEY);
                    })
                    .append(handle -> {
                        Conversation.hide();
                        setFrame(0, 0);
                        freeze(false);
                    })
            );
        }
    }

    public class Justin extends Human {
        public Justin() {
            super("Justin", "images/dystopian_justin.png");
            setPosition(32.0f, 0f);

            setLeft(false);
            setFrame(3, 0);

            setOnTalk(new Sequence()
                    .append(handle -> {
                        freeze(true);
                        setFrame(2, 0);
                        setLeft(!isLeft());
                        handle.next(Trigger.sleep(500));
                    })
                    .append(handle -> {
                        setFrame(4, 0);
                        handle.next(Trigger.sleep(500));
                    })
                    .append(handle -> {
                        setFrame(3, 0);
                        handle.next(Trigger.sleep(500));
                    })
                    .append(handle -> {
                        setLeft(!isLeft());
                        setFrame(2, 0);
                        say(
                                "Yo bro, what a luck!",
                                "dystopian_justin_1.mp3"
                        );
                        handle.next(Trigger.ENTER_KEY);
                    })
                    .append(handle -> {
                        say(
                                "Nowadays there's no need of daily posting our pictures, the world does it for us! You know what I'm saying?",
                                "dystopian_justin_2.mp3"
                        );
                        handle.next(Trigger.ENTER_KEY);
                    })
                    .append(handle -> {
                        say(
                                "Just pose in front of those public cameras and smile to the BDCO!",
                                "dystopian_justin_3.mp3"
                        );
                        handle.next(Trigger.ENTER_KEY);
                    })
                    .append(handle -> {
                        say(
                                "Hope you feel me bro, when I'll be home I'll see this new picture on " +
                                        "Instagram, Facebook, Whatsapp, Google+, Youtube, PSN, as my desktop background, " +
                                        "as my home carpet, EVERYWHERE! You know?",
                                "dystopian_justin_4.mp3"
                        );
                        handle.next(Trigger.ENTER_KEY);
                    })
                    .append(handle -> {
                        say(
                                "THAT'S LIT BRO!",
                                "dystopian_justin_5.mp3"
                        );
                        handle.next(Trigger.ENTER_KEY);
                    })
                    .append(handle -> {
                        setFrame(3, 0);
                        setLeft(!isLeft());
                        freeze(false);
                    })
            );
        }
    }

    public class Lara extends Human {
        public Lara() {
            super("Lara", "images/dystopian_lara.png");
            setPosition(42.0f, 0f);
            setOnTalk(new Sequence()
                    .append(handle -> {
                        freeze(true);
                        say(
                                "Last week my son has been kidnapped by a fool.",
                                "dystopian_lara_1.mp3"
                        );
                        handle.next(Trigger.ENTER_KEY);
                    })
                    .append(handle -> {
                        say(
                                "There's no need of saying that the guilty has been caught some minute after... " +
                                        "he was under the eye of 1000 public cameras.",
                                "dystopian_lara_2.mp3"
                        );
                        handle.next(Trigger.ENTER_KEY);
                    })
                    .append(handle -> {
                        setFrame(2, 0);
                        say(
                                "I LOVE BDCO!",
                                "dystopian_lara_3.mp3"
                        );
                        handle.next(Trigger.sleep(1000));
                    })
                    .append(handle -> {
                        setFrame(0, 0);
                        freeze(false);
                    })
            );
        }
    }

    public class Cassandra extends Human {
        public Cassandra() {
            super("Cassandra", "images/dystopian_cassandra.png");
            setPosition(52.0f, 0f);
            setOnTalk(new Sequence()
                    .append(handle -> {
                        freeze(true);
                        say(
                                "BDCO starts spying you from when you are 8 years old.",
                                "dystopian_cassandra_1.mp3"
                        );
                        handle.next(Trigger.ENTER_KEY);
                    })
                    .append(handle -> {
                        setFrame(3, 0);
                        say(
                                "I'm just fighting to make my 5 years old son spied and protected.",
                                "dystopian_cassandra_2.mp3"
                        );
                        freeze(false);
                    })
            );
        }
    }

    public class Winston extends Human {
        public Winston() {
            super("Winston", "images/dystopian_winston.png");
            setPosition(62.0f, 0f);
        }
    }
}
