package xyz.upperlevel.hgame.world.entity.impl

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.ParticleEffect
import com.badlogic.gdx.math.Vector2
import xyz.upperlevel.hgame.world.World
import xyz.upperlevel.hgame.world.entity.EntityTypes
import xyz.upperlevel.hgame.world.entity.ThrowableEntity
import xyz.upperlevel.hgame.world.sequence.Sequence

class MikrotikEntity(world: World, active: Boolean) : ThrowableEntity(EntityTypes.MIKROTIK, world, active) {
    init {
        // TODO: Animation is started when the entity is created for networking issues.
        Sequence.create()
                .repeat({ _, time -> setFrame(time % 2, 0) }, 100, 8)
                .repeat({ _, time -> setFrame(time % 3 + 2, 0) }, 100, 12)
                .act { setFrame(1, 0) }
                .delay(2000)
                .act { setFrame(0, 0) }
                .delay(500)
                .act {
                    world.despawn(this)
                    explode()
                }
                .play()
    }

    fun explode() {
        val effect = ParticleEffect()
        effect.load(Gdx.files.internal("particles/explosion.p"), Gdx.files.internal("particles"))
        world.doEffect(effect, centerX, centerY)

        val explosion = Vector2(centerX, centerY)
        for (entity in world.entities) {
            val dist = Math.min(
                    explosion.dst(entity.centerX, entity.centerY),
                    explosion.dst(entity.x, entity.y)
            )
            if (dist <= MAX_EXPLOSION_DISTANCE) {
                entity.damage((MAX_EXPLOSION_DISTANCE - dist) / MAX_EXPLOSION_DISTANCE * MAX_EXPLOSION_DAMAGE)
            }
        }
    }

    companion object {
        const val MAX_EXPLOSION_DISTANCE = 3.0f
        const val MAX_EXPLOSION_DAMAGE = 10.0f
    }
}
