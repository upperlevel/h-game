package xyz.upperlevel.hgame.world.character

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import xyz.upperlevel.hgame.world.World


interface EntityType {
    /**
     * The name of the character.
     */
    val name: String

    /**
     * The `Character`'s texture path.
     */
    val texturePath: String

    fun getSprites(texture: Texture): Array<Array<TextureRegion>>

    /**
     * Generates an instance of the [EntityType]'s [Entity].
     * The [Entity] is the object that will populate the Scenario.
     */
    fun personify(id: Int, world: World): Entity
}
