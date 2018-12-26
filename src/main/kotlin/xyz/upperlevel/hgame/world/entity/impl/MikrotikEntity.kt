package xyz.upperlevel.hgame.world.entity.impl

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.ParticleEffect
import xyz.upperlevel.hgame.world.World
import xyz.upperlevel.hgame.world.entity.EntityTypes
import xyz.upperlevel.hgame.world.entity.ThrowableEntity

class MikrotikEntity(world: World) : ThrowableEntity(EntityTypes.MIKROTIK, world) {
    fun explode() {
        val effect = ParticleEffect()
        effect.load(Gdx.files.internal("particles/explosion.p"), Gdx.files.internal("particles"))
        world.doEffect(effect, x + width / 2f, y + height / 2f)
    }
}
