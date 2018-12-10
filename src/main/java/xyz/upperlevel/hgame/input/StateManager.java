package xyz.upperlevel.hgame.input;

import xyz.upperlevel.hgame.world.character.Actor;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class StateManager {
    private final Map<String, Function<Actor, State>> states = new HashMap<>();

    public StateManager() {
    }



    public void register(String id, State state) {
        states.put(id, state);

    }
}
