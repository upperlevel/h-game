package xyz.upperlevel.hgame.world.character

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.physics.box2d.World


interface Character {
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
     * Generates an instance of the [Character]'s [Entity].
     * The [Entity] is the object that will populate the Scenario.
     */
    fun personify(id: Int, pworld: World): Entity
}
