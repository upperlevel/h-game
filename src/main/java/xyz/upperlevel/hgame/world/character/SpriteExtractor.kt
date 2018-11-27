package xyz.upperlevel.hgame.world.character

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion

object SpriteExtractor {
    fun horizontal(texture: Texture, width: Int, height: Int, y: Int, offset: Int, count: Int): Array<TextureRegion> {
        val regY = y / height.toFloat()
        val regWidth = 1.0f / width.toFloat()
        val regHeight = 1.0f / height.toFloat()
        return Array(count) {
            val regX = it * regWidth + offset
            TextureRegion(
                    texture,
                    regX,
                    regY,
                    regX + regWidth,
                    regY + regHeight
            )
        }
    }

    fun grid(texture: Texture, width: Int, height: Int): Array<Array<TextureRegion>> {
        val regWidth = 1.0f / width.toFloat()
        val regHeight = 1.0f / height.toFloat()
        return Array(width) { x ->
            Array(height) { y ->
                TextureRegion(
                        texture,
                        x * regWidth,
                        y * regHeight,
                        x * regWidth + regWidth,
                        y * regHeight + regHeight
                )
            }
        }
    }
}
