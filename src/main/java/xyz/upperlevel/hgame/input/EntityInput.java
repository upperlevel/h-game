package xyz.upperlevel.hgame.input;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.unmodifiableList;

public class EntityInput {
    private final List<InputAction> actions;
    
    public EntityInput(List<InputAction> actions) {
        this.actions = new ArrayList<>(actions);
    }

    public List<InputAction> getActions() {
        return unmodifiableList(actions);
    }
}
