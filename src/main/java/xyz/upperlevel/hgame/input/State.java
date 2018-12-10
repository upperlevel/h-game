package xyz.upperlevel.hgame.input;

import lombok.Getter;
import xyz.upperlevel.hgame.behaviour.Trigger;
import xyz.upperlevel.hgame.world.character.Actor;

import java.util.HashMap;
import java.util.Map;

public class State {
    @Getter
    private final StateManager stateManager;

    @Getter
    private final String id;

    @Getter
    private final Actor actor;

    private final Map<Trigger, State> hooks = new HashMap<>();

    public State(StateManager stateManager, String id, Actor actor) {
        this.stateManager = stateManager;
        this.id = id;
        this.actor = actor;
    }

    public void hook(Trigger trigger, State state) {
        hooks.put(trigger, state);
    }

    public void enable() {
    }

    public void disable() {
    }
}
