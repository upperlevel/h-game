package xyz.upperlevel.hgame.world.character.impl

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import xyz.upperlevel.hgame.event.Listener
import xyz.upperlevel.hgame.world.World
import xyz.upperlevel.hgame.world.character.Entity
import xyz.upperlevel.hgame.world.character.EntityType
import xyz.upperlevel.hgame.world.character.SpriteExtractor
import xyz.upperlevel.hgame.world.character.ThrowableEntity
import com.badlogic.gdx.physics.box2d.World as PhysicsWorld


class Mikrotik : EntityType, Listener {
    override val id = "mikrotik"
    override val texturePath = "mikrotik.png"

    override val width = .5f
    override val height = .5f

    override fun getSprites(texture: Texture): Array<Array<TextureRegion>> {
        return SpriteExtractor.grid(texture, 1, 1)
    }

    override fun create(world: World): Entity {
        val entity = ThrowableEntity(this, world)
        entity.setFrame(0, 0)
        return entity
    }
}
