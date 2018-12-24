package xyz.upperlevel.hgame.hud

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack
import xyz.upperlevel.hgame.world.character.Player


class PlayerStatsDrawer(val flip: Boolean) : BaseDrawable() {
    var player: Player? = null

    init {
        minWidth = containerRegion.regionWidth.toFloat()
        minHeight = containerRegion.regionHeight.toFloat()
    }

    private fun flipTexturesIfNeeded() {
        if (!flip) return
        containerRegion.flip(true, false)
        lifeBarRegion.flip(true, false)
        energyBarRegion.flip(true, false)
    }

    override fun draw(batch: Batch?, x: Float, y: Float, width: Float, height: Float) {
        val p = player ?: return
        batch!!

        // Offsets
        var lifeOffX = barsLeftOffset
        var energyOffX = barsLeftOffset
        val lifeOffY = 12f
        val energyOffY = 3f

        // Scale
        val scaleW = width / containerRegion.regionWidth
        val scaleH = height / containerRegion.regionHeight


        // Where do we need to cut with GL_STENCIL_TEST
        // (from the start of the bars to the end of them)
        val cutX: Float
        val cutW: Float
        val dir: Float// Direction (1f if facing right -1f if facing left)

        if (!flip) {
            // Where is the start of the bar? at energyOffX (scale is multiplied to convert to screen coords)
            cutX = x + energyOffX * scaleW
            // The width is at the end of the given space,
            // and convert it to screen coords
            cutW = width - cutX
            dir = 1f
        } else {
            // When reversed we need to reverse the cut rectangle
            // Start from the x-start
            cutX = x
            // End at the beginning of the reversed bars (that is width - prefix)
            cutW = width - barsLeftOffset * scaleW
            dir = -1f
        }

        var lifePercentage = 1f - p.life / p.maxLife
        var energyPercentage = 0.5f//1f - player.energy

        if (flip) {
            lifeOffX = containerRegion.regionWidth - lifeBarRegion.regionWidth - lifeOffX
            energyOffX = containerRegion.regionWidth - energyBarRegion.regionWidth - energyOffX
        }
        // Apply the percentages to the offsets
        lifeOffX -= lifePercentage * lifeBarRegion.regionWidth * dir
        energyOffX -= energyPercentage * energyBarRegion.regionWidth * dir

        flipTexturesIfNeeded()

        // Now we draw
        batch.draw(containerRegion, x, y, width, height)

        batch.flush()

        // TODO: apply batch matrix
        ScissorStack.pushScissors(Rectangle(cutX, y, cutW, height))
        batch.draw(lifeBarRegion, x + lifeOffX * scaleW, y + lifeOffY * scaleH, lifeBarRegion.regionWidth * scaleW, lifeBarRegion.regionHeight * scaleH)
        batch.draw(energyBarRegion, x + energyOffX * scaleW, y + energyOffY * scaleH, energyBarRegion.regionWidth * scaleW, energyBarRegion.regionHeight * scaleH)

        batch.flush()
        ScissorStack.popScissors()

        // Flip the textures back
        flipTexturesIfNeeded()
    }

    companion object {
        private val texture = Texture(Gdx.files.internal("images/hud.png"))
        private val containerRegion = TextureRegion(texture, 0, 0, 101, 27)
        private val lifeBarRegion = TextureRegion(texture, 0, 30, 82, 9)
        private val energyBarRegion = TextureRegion(texture, 0, 41, 86, 5)

        private const val barsLeftOffset = 12f
    }
}