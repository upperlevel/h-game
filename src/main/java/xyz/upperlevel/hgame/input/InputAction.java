package xyz.upperlevel.hgame.input;

import lombok.AllArgsConstructor;
import lombok.Getter;
import xyz.upperlevel.hgame.scenario.GameScreen;
import xyz.upperlevel.hgame.network.Endpoint;
import xyz.upperlevel.hgame.scenario.character.Actor;

import java.util.function.Consumer;

@AllArgsConstructor
public class InputAction {
    @Getter
    private final Actor actor;
    @Getter
    private final int id;
    @Getter
    private InputTrigger trigger;
    private Consumer<Actor> consequence;

    public void trigger() {
        Endpoint endpoint = GameScreen.instance.getEndpoint();
        if (endpoint.isConnected()) {
            endpoint.send(new TriggerInputActionPacket(actor.getId(), id));
        }
        onTrigger();
    }

    public void onTrigger() {
        consequence.accept(actor);
    }
}
