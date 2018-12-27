package xyz.upperlevel.hgame.world.entity.impl

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.ParticleEffect
import xyz.upperlevel.hgame.world.World
import xyz.upperlevel.hgame.world.entity.EntityTypes
import xyz.upperlevel.hgame.world.entity.ThrowableEntity
import xyz.upperlevel.hgame.world.sequence.Sequence

class PoisonEntity(world: World, active: Boolean) : ThrowableEntity(EntityTypes.POISON, world, active) {
    val effect: ParticleEffect
    val duration = 20000

    // TODO: add damage

    init {
        setFrame(0, 0)

        effect = ParticleEffect().apply {
            load(Gdx.files.internal("particles/poison_bubble.p"), Gdx.files.internal("particles"))
            setDuration(duration)
        }
        effect.emitters.first().spawnWidth.apply {
            highMin = width
            highMax = width
        }

        world.doEffect(effect, x - width / 2f, y + height)

        Sequence.create()
                .delay(duration.toLong())
                .act {
                    world.despawn(this)
                }
                .play()
    }

    fun updatePos() {
        effect.setPosition(x - width / 2f, y)
    }
}