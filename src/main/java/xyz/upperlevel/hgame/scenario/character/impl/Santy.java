package xyz.upperlevel.hgame.scenario.character.impl;

import xyz.upperlevel.hgame.scenario.character.Actor;
import xyz.upperlevel.hgame.scenario.character.Character;
import xyz.upperlevel.hgame.scenario.scheduler.Scheduler;

public class Santy implements Character {
    @Override
    public String getName() {
        return "Santy";
    }

    @Override
    public String getTexturePath() {
        return "prof_santy.png";
    }

    @Override
    public Actor personify() {
        return new ActorImpl(this);
    }

    private class ActorImpl extends Actor {
        private int attackTask = -1;

        // Special attack
        private int shakingTask = -1;
        private int specialAttackTask = -1;

        private ActorImpl(Character character) {
            super(character);
        }

        @Override
        public void move(float offsetX) {
            super.move(offsetX * 0.5f);
        }

        @Override
        public void jump(float strength) {
            super.jump(strength * 0.5f);
        }

        @Override
        public void attack() {
            if (attackTask >= 0) {
                Scheduler.cancel(attackTask);
                attackTask = -1;
            }
            setFrame(0, 2);
            attackTask = Scheduler.start(() -> {
                setFrame(1, 2);
                attackTask = Scheduler.start(() -> {
                    setFrame(0, 0);
                }, 200);
            }, 200);
        }

        @Override
        public void specialAttack() {
            if (shakingTask < 0) {
                Scheduler.cancel(shakingTask);
                shakingTask = -1;
            }

            if (specialAttackTask >= 0) {
                Scheduler.cancel(specialAttackTask);
                specialAttackTask = -1;
            }

            shakingTask = Scheduler.start(new ShakingTask(), 500, true);
        }

        private class ShakingTask implements Runnable {
            private int times = 10;

            @Override
            public void run() {
                setFrame(times % 2, 3);
                if (--times <= 0) {
                    Scheduler.cancel(shakingTask);
                    shakingTask = -1;

                    specialAttackTask = Scheduler.start(new SpecialAttackTask(), 500, true);
                }
            }
        }

        private class SpecialAttackTask implements Runnable {
            private int frame = 2;

            @Override
            public void run() {
                setFrame(frame, 3);
                if (++frame > 5) {
                    Scheduler.cancel(specialAttackTask);
                    specialAttackTask = -1;
                }
            }
        }
    }
}
