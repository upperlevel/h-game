package xyz.upperlevel.hgame.scenario;

import com.badlogic.gdx.Gdx;
import xyz.upperlevel.hgame.HGame;
import xyz.upperlevel.hgame.scenario.animation.Sequence;
import xyz.upperlevel.hgame.scenario.animation.Trigger;
import xyz.upperlevel.hgame.scenario.character.Human;

public class UtopianScene extends Scenario {
    private Human google;
    private Human mark;
    private Human gazzy;
    private Human utopinelli;

    public UtopianScene() {
        spawn(google = new Human("Google Director", "images/google_director.png"));
        spawn(mark = new Human("Mark Zuckerberg", "images/mark_zuckerberg.png"));
        spawn(gazzy = new Human("Gazzy Garcia (Lil Pump)", "images/gazzy_garcia.png"));
        spawn(utopinelli = new Human("Paolo Utopinelli", "images/paolo_utopinelli.png"));

        player.setPosition(0, 0);

        google.setPosition(7, 0);
        google.setLeft(true);

        mark.setPosition(20, 0);

        gazzy.setPosition(30, 0);

        utopinelli.setPosition(40, 0);
        utopinelli.setLeft(true);

        player.say(
                "Where the... I am...? It seems all to be lighter than before...",
                "utopian_player_12.mp3"
        );

        Event.watch(Event.greaterX(player, 5), () -> {
            freeze(true);
            new Sequence()
                    .append(handle -> {
                        google.setFrame(3, 0);
                        google.say(
                                "Hi kind player, I'm the director of the company \"Google\".",
                                "utopian_gdirector_1.mp3"
                        );
                        handle.next(Trigger.ENTER_KEY);
                    })
                    .append(handle -> {
                        player.setFrame(3, 0);
                        player.say(
                                "Yeah, I already know who you are I just met you in the other level.",
                                "utopian_player_1.mp3"
                        );
                        handle.next(Trigger.ENTER_KEY);
                    })
                    .append(handle -> {
                        player.setFrame(0, 0);
                        google.setFrame(2, 0);
                        google.say(
                                "No no no, I'm not THAT Google Director, I'm the one from the Utopic World, nothing here is similar to the other level.",
                                "utopian_gdirector_2.mp3"
                        );
                        handle.next(Trigger.ENTER_KEY);
                    })
                    .append(handle -> {
                        google.say(
                                "We try to help people in their ordinary lives without spying on them.",
                                "utopian_gdirector_10.mp3"
                        );
                        handle.next(Trigger.ENTER_KEY);
                    })
                    .append(handle -> {
                        google.setFrame(0, 0);
                        player.say(
                                "How is it possible and why are you here?",
                                "utopian_player_2.mp3"
                        );
                        handle.next(Trigger.ENTER_KEY);
                    })
                    .append(handle -> {
                        google.walkTo(player, 0.05f, () -> {
                            handle.next(Trigger.sleep(500));
                        });
                    })
                    .append(handle -> {
                        google.setFrame(2, 0);
                        google.say(
                                "I'm here to take you around to know the others utopic companies directors.",
                                "utopian_gdirector_3.mp3"
                        );
                        handle.next(Trigger.ENTER_KEY);
                    })
                    .append(handle -> {
                        google.setFrame(0, 0);
                        google.say(
                                "You'll meet some personalities such as Mark Zuckerberg, Gazzy Garcia, Paolo Utopinelli..." +
                                        "*every reference is pretty random*",
                                "utopian_gdirector_4.mp3"
                        );
                        handle.next(Trigger.ENTER_KEY);
                    })
                    .append(handle -> {
                        player.setFrame(3, 0);
                        player.say(
                                "Wait, who is he?",
                                "utopian_player_3.mp3"
                        );
                        handle.next(Trigger.ENTER_KEY);
                    })
                    .append(handle -> {
                        player.setFrame(0, 0);
                        google.say(
                                "What!? Don't you know him already? Ehm... well you'll know...",
                                "utopian_gdirector_5.mp3"
                        );
                        handle.next(Trigger.ENTER_KEY);
                    })
                    .append(handle -> {
                        Conversation.hide();
                        handle.next(Trigger.and(player.walkTo(mark, 0.05f), google.walkTo(mark.getX() + 1, 0.05f)));
                    })
                    // Mark Zuckerberg
                    .append(handle -> {
                        google.setLeft(true);
                        google.setFrame(3, 0);
                        google.say(
                                "Hi Mark, I'm here with one \"tourist\" that wants to discover more about our world. " +
                                        "Can you explain what does your company do?",
                                "utopian_gdirector_6.mp3"
                        );
                        handle.next(Trigger.ENTER_KEY);
                    })
                    .append(handle -> {
                        google.setFrame(0, 0);

                        mark.setLeft(true);
                        mark.setFrame(3, 0);
                        mark.say(
                                "Well Facebook, my company, finds his main goal in making people interaction with each other more open and true " +
                                        "with a simple motto: \"less prejudices\".",
                                "utopian_zuckerberg_1.mp3"
                        );
                        handle.next(Trigger.ENTER_KEY);
                    })
                    .append(handle -> {
                        mark.say(
                                "We don't have to concentrate on reducing race or sexual discrimination on our platform.",
                                "utopian_zuckerberg_2.mp3"
                        );
                        handle.next(Trigger.ENTER_KEY);
                    })
                    .append(handle -> {
                        mark.say(
                                "People here live in a utopic world so they are completely open-minded to every realty that is different to their.",
                                "utopian_zuckerberg_3.mp3"
                        );
                        handle.next(Trigger.ENTER_KEY);
                    })
                    .append(handle -> {
                        mark.say(
                                "We also reached a very high level of trust with our users, the code of the application is open-source " +
                                        "so that everyone could know what his info are used for.",
                                "utopian_zuckerberg_4.mp3"
                        );
                        handle.next(Trigger.ENTER_KEY);
                    })
                    .append(handle -> {
                        mark.setFrame(0, 0);

                        player.say(
                                "Oh my god! It's impressive. I really wish that the real society could see things as this one do. But, probably, this will never happen...",
                                "utopian_player_4.mp3"
                        );
                        handle.next(Trigger.ENTER_KEY);
                    })
                    .append(handle -> {
                        mark.setFrame(2, 0);

                        mark.say(
                                "Nothing is impossible my dear friend, wait to see Paolo Utopinelli and you will agree with me. " +
                                        "What he did was magnificent, he...",
                                "utopian_zuckerberg_5.mp3"
                        );
                        handle.next(Trigger.ENTER_KEY);
                    })
                    .append(handle -> {
                        mark.setFrame(0, 0);

                        google.setVelocity(0, 3);
                        handle.next(Trigger.sleep(1000));
                    })
                    .append(handle -> {
                        google.setFrame(2, 0);
                        google.say(
                                "NO MARK WAIT! He doesn't know him... let the suspence live please. Hey protagonist, come on, we have to continue our visit...",
                                "utopian_gdirector_7.mp3"
                        );
                        handle.next(Trigger.ENTER_KEY);
                    })
                    .append(handle -> {
                        google.setFrame(0, 0);
                        Conversation.hide(); // removes conversation audio and text

                        handle.next(Trigger.and(player.walkTo(gazzy.getX() + 1, 0.05f), google.walkTo(gazzy.getX() + 3, 0.05f)));
                    })
                    // Gazzy Garcia (Lil Pump)
                    .append(handle -> {
                        gazzy.setFrame(2, 0);
                        gazzy.say(
                                "Hi protagonist, I KNOW WHY YOU'RE HERE...",
                                "utopian_pump_1.mp3"
                        );
                        handle.next(Trigger.ENTER_KEY);
                    })
                    .append(handle -> {
                        google.setLeft(true);
                        player.setLeft(true);

                        google.setVelocity(0, 3);

                        handle.next(Trigger.sleep(1000));
                    })
                    .append(handle -> {
                        player.setFrame(2, 0);
                        player.say(
                                "Yeah, I knew it was too perfect to be real, also in this universe you spy on people movements...",
                                "utopian_player_5.mp3"
                        );
                        handle.next(Trigger.ENTER_KEY);
                    })
                    .append(handle -> {
                        player.setFrame(0, 0);

                        gazzy.setFrame(3, 0);
                        gazzy.say(
                                "I'm sorry mate, you're wrong... well, in a part. " +
                                        "We track on people movements only to help them.",
                                "utopian_pump_2.mp3"
                        );
                        handle.next(Trigger.ENTER_KEY);
                    })
                    .append(handle -> {
                        gazzy.setFrame(0, 0);

                        player.setLeft(false);
                        google.setFrame(3, 0);
                        google.say(
                                "You have to know that what Gazzy is trying to say is that they don't track movements to sell your info or something similar.",
                                "utopian_gdirector_8.mp3"
                        );
                        handle.next(Trigger.ENTER_KEY);
                    })
                    .append(handle -> {
                        google.say(
                                "They just do it to shut off the lights if you go out and forget to do that or...",
                                "utopian_gdirector_9.mp3"
                        );
                        handle.next(Trigger.ENTER_KEY);
                    })
                    .append(handle -> {
                        google.setFrame(0, 0);

                        player.setLeft(true);
                        player.setVelocity(0, 3);

                        gazzy.setFrame(2, 0);
                        gazzy.say(
                                "... to even close your house door if you don't do that yourself!",
                                "utopian_pump_3.mp3"
                        );
                        handle.next(Trigger.ENTER_KEY);
                    })
                    .append(handle -> {
                        gazzy.setFrame(3, 0);
                        gazzy.say(
                                "And you also have to know that every info is kept under security by protections that doesn't " +
                                        "allow hackers (but that doesn't exist in our utopic world) to see where you are.",
                                "utopian_pump_4.mp3"
                        );
                        handle.next(Trigger.ENTER_KEY);
                    })
                    .append(handle -> {
                        gazzy.setFrame(0, 0);

                        player.setFrame(2, 0);
                        player.say(
                                "This is revolutionary! I can't find any critic to your world.",
                                "utopian_player_6.mp3"
                        );
                        handle.next(Trigger.ENTER_KEY);
                    })
                    .append(handle -> {
                        player.say(
                                "Is so perfect and tell me, is your code accessible to everyone like Facebook one is?",
                                "utopian_player_7.mp3"
                        );
                        handle.next(Trigger.ENTER_KEY);
                    })
                    .append(handle -> {
                        player.setFrame(0, 0);

                        gazzy.setFrame(3, 0);
                        gazzy.say(
                                "Obviously it is, EVERY application here must be open-source. Utopinelli decided it.",
                                "utopian_pump_5.mp3"
                        );
                        handle.next(Trigger.ENTER_KEY);
                    })
                    .append(handle -> {
                        gazzy.setFrame(0, 0);

                        player.setFrame(4, 0);
                        player.say(
                                "Fantastic, I can't wait to see this famous Utopinelli. It must be an hero.",
                                "utopian_player_8.mp3"
                        );
                        handle.next(Trigger.ENTER_KEY);
                    })
                    .append(handle -> {
                        handle.next(Trigger.and(player.walkTo(utopinelli.getX() - 2, 0.05f), google.walkTo(utopinelli.getX() - 3, 0.05f)));
                    })
                    // Paolo Utopinelli
                    .append(handle -> {
                        google.setFrame(2, 0);
                        google.say(
                                "So player, we arrived at the end of our journey. This is the famous...",
                                "utopian_gdirector_11.mp3"
                        );
                        handle.next(Trigger.ENTER_KEY);
                    })
                    .append(handle -> {
                        google.setFrame(0, 0);

                        player.setLeft(true);
                        player.setVelocity(0, 7);
                        player.say(
                                "PAOLO UTOPINELLI! <3",
                                "utopian_player_9.mp3"
                        );
                        handle.next(Trigger.ENTER_KEY);
                    })
                    .append(handle -> {
                        google.setFrame(3, 0);
                        player.say(
                                "Yeah, exactly. Now go, ask him whatever you want.",
                                "utopian_gdirector_12.mp3"
                        );
                        handle.next(Trigger.ENTER_KEY);
                    })
                    .append(handle -> {
                        google.setFrame(0, 0);

                        handle.next(player.walkTo(utopinelli, 0.05f));
                    })
                    .append(handle -> {
                        player.setFrame(3, 0);
                        player.say(
                                "Hi Sir, a lot of people told me about you, but now tell me what is your role here.",
                                "utopian_player_10.mp3"
                        );
                        handle.next(Trigger.ENTER_KEY);
                    })
                    .append(handle -> {
                        player.setFrame(0, 0);

                        utopinelli.setFrame(3, 0);
                        utopinelli.say(
                                "So, how can I start... well, let's say that I'm the SUPERVISOR of the internet world, in this utopic world.",
                                "utopian_utopinelli_1.mp3"
                        );
                        handle.next(Trigger.ENTER_KEY);
                    })
                    .append(handle -> {
                        utopinelli.setFrame(3, 0);
                        utopinelli.say(
                                "I have to control whether if companies respect our internet rules and I have to close those companies if they don't do that...",
                                "utopian_utopinelli_2.mp3"
                        );
                        handle.next(Trigger.ENTER_KEY);
                    })
                    .append(handle -> {
                        utopinelli.setFrame(2, 0);
                        utopinelli.say(
                                "But this never happens as you could imagine because...",
                                "utopian_utopinelli_3.mp3"
                        );
                        handle.next(Trigger.ENTER_KEY);
                    })
                    .append(handle -> {
                        utopinelli.setFrame(0, 0);

                        player.setFrame(3, 0);
                        player.say(
                                "Yes because we are in a utopic world and everything is perfect. " +
                                        "I really love this reality. I wish if I can stay and live here, could I?",
                                "utopian_player_11.mp3"
                        );
                        handle.next(Trigger.ENTER_KEY);
                    })
                    .append(handle -> {
                        player.setFrame(0, 0);

                        utopinelli.setFrame(2, 0);
                        utopinelli.say(
                                "YES, OF COURSE YOU CAN! You won't repent it... OuTPuTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT!!!",
                                "utopian_utopinelli_4.mp3"
                        );
                        handle.next(Trigger.ENTER_KEY);
                    })
                    .append(handle -> {
                        Conversation.hide();
                        utopinelli.setFrame(0, 0);
                        handle.next(Trigger.sleep(3 * 1000));
                    })
                    .append(handle -> {
                        freeze(false);
                        utopinelli.setVelocity(0, 100);
                        handle.next(Trigger.sleep(5 * 1000));
                    })
                    .append(handle -> {
                        HGame.instance.getStoryline().scenario = new GameOverScene();
                    })
                    .play();

        });
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f); // white
        super.render();
    }
}
