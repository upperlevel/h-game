package xyz.upperlevel.hgame.input;

import com.badlogic.gdx.Input;
import xyz.upperlevel.hgame.world.character.Actor;

import java.util.Arrays;
import java.util.function.Consumer;

import static xyz.upperlevel.hgame.world.World.ACTOR_JUMP_SPEED;
import static xyz.upperlevel.hgame.world.World.ACTOR_MOVE_SPEED;

public final class StandardEntityInput {
    public static final Consumer<Actor> MOVE_LEFT = a -> a.move(-ACTOR_MOVE_SPEED);
    public static final Consumer<Actor> MOVE_RIGHT = a -> a.move(ACTOR_MOVE_SPEED);
    public static final Consumer<Actor> JUMP = a -> a.jump(ACTOR_JUMP_SPEED);

    public static EntityInput create(Actor actor) {
        return new EntityInput(Arrays.asList(
                new InputAction(// Left
                        actor,
                        0,
                        InputTrigger.onKeyDown(Input.Keys.A),
                        MOVE_LEFT
                ),
                new InputAction(// Right
                        actor,
                        1,
                        InputTrigger.onKeyDown(Input.Keys.D),
                        MOVE_RIGHT
                ),
                new InputAction(// Jump
                        actor,
                        2,
                        InputTrigger.onKeyDown(Input.Keys.W).and(i -> actor.isTouchingGround()),
                        JUMP
                ),
                new InputAction(
                        actor,
                        3,
                        InputTrigger.onKeyPress(Input.Keys.SPACE),
                        Actor::attack
                ),
                new InputAction(
                        actor,
                        4,
                        InputTrigger.onKeyPress(Input.Keys.J),
                        Actor::specialAttack
                )
        ));
    }

    private StandardEntityInput() {}
}
