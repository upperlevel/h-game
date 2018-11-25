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

    public void onNetworkAction(int actionType) {
        actions.stream()
                .filter(a -> a.getId() == actionType)
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Illegal action: " + actionType))
                .onTrigger();
    }
}
