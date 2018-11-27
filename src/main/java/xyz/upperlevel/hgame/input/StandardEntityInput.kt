package xyz.upperlevel.hgame.input

import com.badlogic.gdx.Input
import xyz.upperlevel.hgame.world.World.Companion.ACTOR_JUMP_SPEED
import xyz.upperlevel.hgame.world.World.Companion.ACTOR_MOVE_SPEED
import xyz.upperlevel.hgame.world.character.Actor
import java.util.*

object StandardEntityInput {
    val MOVE_LEFT: Consequence = { it.move(-ACTOR_MOVE_SPEED) }
    val MOVE_RIGHT: Consequence = { it.move(ACTOR_MOVE_SPEED) }
    val JUMP: Consequence = { it.jump(ACTOR_JUMP_SPEED) }

    fun create(actor: Actor): EntityInput {
        return EntityInput(Arrays.asList(
                InputAction (// Left
                        actor,
                        0,
                        InputTriggers.onKeyDown(Input.Keys.A),
                        MOVE_LEFT
                ),
                InputAction(// Right
                        actor,
                        1,
                        InputTriggers.onKeyDown(Input.Keys.D),
                        MOVE_RIGHT
                ),
                InputAction(// Jump
                        actor,
                        2,
                        InputTriggers.onKeyDown(Input.Keys.W).and { actor.isTouchingGround },
                        JUMP
                ),
                InputAction(
                        actor,
                        3,
                        InputTriggers.onKeyPress(Input.Keys.SPACE)
                ) { obj: Actor -> obj.attack() },
                InputAction(
                        actor,
                        4,
                        InputTriggers.onKeyPress(Input.Keys.J)
                ) { obj: Actor -> obj.specialAttack() }
        ))
    }
}
