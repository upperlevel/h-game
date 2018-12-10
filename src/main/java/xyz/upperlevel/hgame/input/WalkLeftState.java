package xyz.upperlevel.hgame.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import xyz.upperlevel.hgame.world.character.Actor;

public class WalkLeftState extends State {
    public WalkLeftState(StateManager stateManager, String id, Actor actor) {
        super(stateManager, id, actor);
    }

    @Override
    public void enable() {
        hook(() -> Gdx.input.isKeyPressed(Input.Keys.A), getStateManager().get)

    }
}
