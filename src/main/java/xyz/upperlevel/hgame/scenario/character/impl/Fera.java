package xyz.upperlevel.hgame.scenario.character.impl;

import xyz.upperlevel.hgame.scenario.character.Actor;
import xyz.upperlevel.hgame.scenario.character.Character;

public class Fera implements Character {
    @Override
    public String getName() {
        return "Marianna";
    }

    @Override
    public String getSurname() {
        return "Fera";
    }

    @Override
    public String getNickname() {
        return "Fera";
    }

    @Override
    public String getTexturePath() {
        return "prof_marianna_fera.png";
    }

    @Override
    public Actor personify(int id) {
        return new ActorImpl(id, this);
    }

    private class ActorImpl extends Actor {
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
        public void specialAttack() {
            // TODO
            System.out.println(getFormalName() + ": my special attack!");
        }
    }
}
