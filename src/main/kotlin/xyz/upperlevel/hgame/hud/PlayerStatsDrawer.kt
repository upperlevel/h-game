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
        if (player == null) return
        batch!!

        // Offsets
        var lifeOffX = 12f
        var energyOffX = 12f
        val lifeOffY = 12f
        val energyOffY = 3f

        // Scale
        val scaleW = width / containerRegion.regionWidth
        val scaleH = height / containerRegion.regionHeight


        // Where do we need to cut with GL_STENCIL_TEST
        // (from the start of the bars on the left to the start of the reversed bars on the right)
        // Where is the start of the bar? at energyOffX (scale and width are multiplied to convert to screen coords)
        val cutX: Float = x + energyOffX * scaleW
        // Where is the end of the reversed bar? at (1.0 - energyOffX), duplicate the length (it needs to be a width)
        // and convert it to screen coords
        val cutW = width - energyOffX * 2 * scaleW

        var dir = 1f// Direction (1f if facing right -1f if facing left)

        var lifePercentage = 0.5f//player.life
        var energyPercentage = 0.5f//player.energy

        if (flip) {
            lifeOffX = -lifeBarRegion.regionWidth - lifeOffX
            energyOffX = -energyBarRegion.regionWidth - energyOffX

            // If we draw in reverse then we need to inverse the percentages
            // because they describe how much padding we need on the left (so the amount of empty space)
            lifePercentage = 1f - lifePercentage
            energyPercentage = 1f - energyPercentage

            dir = -1f
        }
        // Apply the percentages to the offsets
        lifeOffX -= lifePercentage * lifeBarRegion.regionWidth * dir
        energyOffX -= energyPercentage * energyBarRegion.regionWidth * dir

        flipTexturesIfNeeded()

        // Now we draw
        batch.draw(containerRegion, x, y, width, height)

        batch.flush()

        // TODO: apply batch matrix
        var res = ScissorStack.pushScissors(Rectangle(cutX, y, cutW, height))
        if (!res) throw IllegalStateException("Fucking scissors")
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
    }
}