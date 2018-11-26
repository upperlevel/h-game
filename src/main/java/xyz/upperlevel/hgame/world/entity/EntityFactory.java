package xyz.upperlevel.hgame.world.entity;

import xyz.upperlevel.hgame.world.character.Actor;

public interface EntityFactory<E extends Actor> {
    E personify(int id);
}
