package xyz.upperlevel.hgame.scenario.entity;

import xyz.upperlevel.hgame.scenario.character.Actor;

public interface EntityFactory<E extends Actor> {
    E personify(int id);
}
