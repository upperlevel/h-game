package xyz.upperlevel.hgame.world

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.Align
import org.apache.logging.log4j.LogManager
import org.lwjgl.opengl.GL11
import xyz.upperlevel.hgame.DefaultFont
import xyz.upperlevel.hgame.world.character.Player
import xyz.upperlevel.hgame.world.entity.EntitySpawnEvent

class HudRenderer(world: World) {
    var leftPlayer: Player? = null
    var rightPlayer: Player? = null

    private val texture = Texture(Gdx.files.internal("images/hud.png"))
    private val containerRegion = TextureRegion(texture, 0, 0, 101, 27)
    private val lifeBarRegion = TextureRegion(texture, 0, 30, 82, 9)
    private val energyBarRegion = TextureRegion(texture, 0, 41, 86, 5)

    private val padX: Float = 0f
    private val padY: Float = 0f
    private val contanierMargin: Float = 0.01f
    private val containerYPadding: Float

    private val batch = SpriteBatch()
    private val textBatch = SpriteBatch()

    private val leftNameLabel: Label
    private val rightNameLabel: Label

    private val logger = LogManager.getLogger()

    init {
        world.events.register(EntitySpawnEvent::class.java, { e ->
            if (e.entity !is Player) return@register
            onPlayerFind(e.entity)
        })
        world.entities.filter {it is Player}
                .forEach { onPlayerFind(it as Player) }

        batch.projectionMatrix = Matrix4().apply {
            setToOrtho2D(0f, 0f, 1f, 1f)
        }

        val skin = Skin().apply {
            var labelStyle = Label.LabelStyle()
            labelStyle.font = DefaultFont.FONT
            add("default", labelStyle)
        }

        val textH = DefaultFont.FONT.capHeight
        val maxTextW = Gdx.graphics.width / 2f
        leftNameLabel = Label("Ulisse", skin)
        leftNameLabel.setBounds(padX, padY + Gdx.graphics.height.toFloat() - textH, maxTextW, textH)

        rightNameLabel = Label("Ulisse", skin)
        rightNameLabel.setBounds(padX + maxTextW, padY + Gdx.graphics.height.toFloat() - textH, maxTextW, textH)
        rightNameLabel.setAlignment(Align.right)

        containerYPadding = padY + textH / Gdx.graphics.height + contanierMargin
    }

    private fun onPlayerFind(p: Player) {
        logger.info("found player: ${p.left}")

        // TODO: change labels
        if (p.left) {
            leftPlayer = p
        } else {
            rightPlayer = p
        }
    }

    private fun drawTex(region: TextureRegion, x: Float, y: Float, scale: Float) {
        val h = region.regionHeight * scale
        batch.draw(region, x, y - h, region.regionWidth * scale, h)
    }

    private fun drawName(left: Boolean, right: Boolean) {
        textBatch.begin()
        if (left) leftNameLabel.draw(textBatch, 1.0f)
        if (right) rightNameLabel.draw(textBatch, 1.0f)
        textBatch.end()
    }

    private fun drawPlayer(lifePercentage: Float, energyPercentage: Float, flip: Boolean) {
        if (flip) {
            containerRegion.flip(true, false)
            lifeBarRegion.flip(true, false)
            energyBarRegion.flip(true, false)
        }

        val scale = 0.005f

        // Offsets
        var containerOffX = 0f
        var lifeOffX = 12f
        var energyOffX = 12f
        val lifeOffY = 6f
        val energyOffY = 19f

        var dir = 1f// Direction (1f if facing right -1f if facing left)

        // Where do we need to cut with GL_STENCIL_TEST
        // (from the start of the bars on the left to the start of the reversed bars on the right)
        // Where is the start of the bar? at energyOffX (scale and width are multiplied to convert to screen coords)
        val cutX: Int = (energyOffX * scale * Gdx.graphics.width).toInt()
        // Where is the end of the reversed bar? at (1.0 - energyOffX), duplicate the length (it needs to be a width)
        // and convert it to screen coords
        val cutW = Math.ceil((1.0 - energyOffX * 2 * scale) * Gdx.graphics.width).toInt()

        var lifePercDraw = lifePercentage
        var energyPercDraw = energyPercentage

        var x = padX
        var y = 1.0f - containerYPadding

        if (flip) {
            // Reverse every offset (they describe the space left from the right)
            containerOffX = -containerRegion.regionWidth.toFloat()
            lifeOffX = -lifeBarRegion.regionWidth - lifeOffX
            energyOffX = -energyBarRegion.regionWidth - energyOffX

            // If we draw in reverse then we need to inverse the percentages
            // because they describe how much padding we need on the left (so the amount of empty space)
            lifePercDraw = 1f - lifePercDraw
            energyPercDraw = 1f - energyPercDraw

            x = 1f - x
            dir = -1f
        }

        // Apply the percentages to the offsets
        lifeOffX -= lifePercDraw * lifeBarRegion.regionWidth * dir
        energyOffX -= energyPercDraw * energyBarRegion.regionWidth * dir


        // Finally begin drawing
        batch.begin()

        // Main life & energy container
        drawTex(containerRegion, x + containerOffX * scale, y, scale)

        // Now we need to enable scissor testing so we need to flush what we drew until now
        batch.flush()
        // Enable scissor testing with the values calculated before
        Gdx.gl.glEnable(GL11.GL_SCISSOR_TEST)
        Gdx.gl.glScissor(cutX, 0, cutW, Gdx.graphics.height)

        // Draw scissored life and energy
        drawTex(lifeBarRegion, x + lifeOffX * scale, y - lifeOffY * scale, scale)
        drawTex(energyBarRegion, x + energyOffX * scale, y - energyOffY * scale, scale)

        batch.end()
        Gdx.gl.glDisable(GL11.GL_SCISSOR_TEST)

        if (flip) {
            // Re-flip the regions so that they are back to normal
            containerRegion.flip(true, false)
            lifeBarRegion.flip(true, false)
            energyBarRegion.flip(true, false)
        }
    }

    fun render(renderer: WorldRenderer) {
        // TODO: attach life and energy
        leftPlayer?.let {
            drawPlayer(0.5f, 0.5f, false)
        }
        rightPlayer?.let{
            drawPlayer(0.3f, 0.7f, true)
        }
        drawName(leftPlayer != null, rightPlayer != null)
    }
}