package xyz.upperlevel.hgame.world.entity

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion

object SpriteExtractor {
    fun one(texture: Texture, x: Int, y: Int, width: Int, height: Int): TextureRegion {
        return TextureRegion(
                texture,
                x / texture.width.toFloat(),
                y / texture.height.toFloat(),
                width / texture.width.toFloat(),
                height / texture.height.toFloat()
        )
    }

    fun horizontal(texture: Texture, width: Int, height: Int, y: Int, count: Int, offset: Int = 0): Array<TextureRegion> {
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
