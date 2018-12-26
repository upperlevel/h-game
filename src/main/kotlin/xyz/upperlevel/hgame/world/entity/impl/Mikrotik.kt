package xyz.upperlevel.hgame.world.entity.impl

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import xyz.upperlevel.hgame.event.Listener
import xyz.upperlevel.hgame.world.World
import xyz.upperlevel.hgame.world.entity.Entity
import xyz.upperlevel.hgame.world.entity.EntityType
import xyz.upperlevel.hgame.world.entity.SpriteExtractor
import xyz.upperlevel.hgame.world.entity.ThrowableEntity
import com.badlogic.gdx.physics.box2d.World as PhysicsWorld


class Mikrotik : EntityType, Listener {
    override val id = "mikrotik"
    override val texturePath = "mikrotik.png"

    override val width = .5f
    override val height = .5f

    override fun getSprites(texture: Texture): Array<Array<TextureRegion>> {
        return SpriteExtractor.grid(texture, 5, 1)
    }

    override fun create(world: World, active: Boolean): Entity {
        val entity = MikrotikEntity(world, active)
        entity.setFrame(0, 0)
        return entity
    }
}
