package xyz.upperlevel.hgame.scenario.character.impl;

import xyz.upperlevel.hgame.scenario.character.Actor;
import xyz.upperlevel.hgame.scenario.character.Character;

public class Santinelli implements Character {
    @Override
    public String getName() {
        return "Paolo";
    }

    @Override
    public String getSurname() {
        return "Santinelli";
    }

    @Override
    public String getNickname() {
        return "Santinelli";
    }

    @Override
    public String getTexturePath() {
        return "prof_paolo_santinelli.png";
    }

    @Override
    public Actor personify() {
        return new ActorImpl(this);
    }

    private class ActorImpl extends Actor {
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
        public void specialAttack() {
            // TODO ATTACK OF THE BINARY CODE!
            System.out.println(getFormalName() + ": attack of the binary code!");
        }
    }
}
