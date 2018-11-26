package xyz.upperlevel.hgame.world.character.impl;

import xyz.upperlevel.hgame.world.character.Actor;
import xyz.upperlevel.hgame.world.character.Character;
import xyz.upperlevel.hgame.world.sequence.Sequence;

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
    public ActorImpl personify(int id) {
        return new ActorImpl(id, this);
    }

    public class ActorImpl extends Actor {
        private int attackTask = -1;

        // Special attack
        private int shakingTask = -1;
        private int specialAttackTask = -1;

        private ActorImpl(int id, Character character) {
            super(id, character);
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
            animate(
                    Sequence.create()
                            .act(() -> setFrame(0, 2))
                            .delay(200)
                            .act(() -> setFrame(1, 2))
                            .delay(200)
                            .act(() -> setFrame(0, 0))
            );
        }

        @Override
        public void specialAttack() {
            animate(
                    Sequence.create()
                            .repeat((step, time) -> setFrame(time % 2, 3), 200, 15)
                            .repeat((step, time) -> setFrame(time + 2, 3), 500, 2)
                            .repeat((step, time) -> setFrame(time + 4, 3), 200, 5)
                            .delay(2000)
                            .act(() -> setFrame(0, 0))
            );
        }
    }
}
