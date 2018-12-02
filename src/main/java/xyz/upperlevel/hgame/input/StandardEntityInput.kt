package xyz.upperlevel.hgame.input

import com.badlogic.gdx.Input
import xyz.upperlevel.hgame.world.World.Companion.ACTOR_JUMP_SPEED
import xyz.upperlevel.hgame.world.World.Companion.ACTOR_MOVE_SPEED
import xyz.upperlevel.hgame.world.character.Entity
import java.util.*

object StandardEntityInput {
    val MOVE_LEFT: Consequence = { it.move(-ACTOR_MOVE_SPEED) }
    val MOVE_RIGHT: Consequence = { it.move(ACTOR_MOVE_SPEED) }
    val JUMP: Consequence = { it.jump(ACTOR_JUMP_SPEED) }

    fun create(entity: Entity): EntityInput {
        return EntityInput(Arrays.asList(
                InputAction (// Left
                        entity,
                        0,
                        InputTriggers.onKeyDown(Input.Keys.A),
                        MOVE_LEFT
                ),
                InputAction(// Right
                        entity,
                        1,
                        InputTriggers.onKeyDown(Input.Keys.D),
                        MOVE_RIGHT
                ),
                InputAction(// Jump
                        entity,
                        2,
                        InputTriggers.onKeyDown(Input.Keys.W).and { entity.isTouchingGround },
                        JUMP
                ),
                InputAction(
                        entity,
                        3,
                        InputTriggers.onKeyPress(Input.Keys.SPACE)
                ) { obj: Entity -> obj.attack() },
                InputAction(
                        entity,
                        4,
                        InputTriggers.onKeyPress(Input.Keys.J)
                ) { obj: Entity -> obj.specialAttack() }
        ))
    }
}
