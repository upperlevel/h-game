package xyz.upperlevel.hgame.world.player

import com.badlogic.gdx.graphics.g2d.TextureRegion
import xyz.upperlevel.hgame.world.entity.EntityType

abstract class PlayerEntityType : EntityType() {
    open val idleRegions: Array<TextureRegion>
        get() = arrayOf(regions[0][0], regions[1][0])
}