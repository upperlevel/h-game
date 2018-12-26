package xyz.upperlevel.hgame.world.entity.impl

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.ParticleEffect
import com.badlogic.gdx.graphics.g2d.TextureRegion
import xyz.upperlevel.hgame.world.World
import xyz.upperlevel.hgame.world.entity.EntityType
import xyz.upperlevel.hgame.world.entity.EntityTypes
import xyz.upperlevel.hgame.world.entity.Player
import xyz.upperlevel.hgame.world.entity.SpriteExtractor
import xyz.upperlevel.hgame.world.sequence.Sequence


class Elisa : EntityType {
    override val id = "elisa"
    override val texturePath = "elisa.png"

    override val width = 2f
    override val height = 2f

    override fun getSprites(texture: Texture): Array<Array<TextureRegion>> {
        return SpriteExtractor.grid(texture, 4, 4)
    }

    override fun create(world: World, active: Boolean): ElisaEntity {
        return ElisaEntity(world, active)
    }
}

class ElisaEntity(world: World, active: Boolean) : Player(EntityTypes.ELISA, world, active) {
    override fun attack(): Sequence {
        return Sequence.create()
                .repeat({ _, time -> setFrame(time, 2) }, 250, 3)
                .act {
                    // TODO: throw the number
                }
    }

    override fun specialAttack(): Sequence {
        return Sequence.create()
                .repeat({ _, time -> setFrame(time, 3) }, 250, 4)
                .act {
                    val effect = ParticleEffect()
                    effect.load(Gdx.files.internal("particles/health.p"), Gdx.files.internal("particles"))
                    world.doEffect(effect, centerX, y + height)
                }
                .delay(3000)
                .act { life = maxLife }
    }
}
