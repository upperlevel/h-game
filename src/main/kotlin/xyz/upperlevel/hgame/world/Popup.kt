package xyz.upperlevel.hgame.world

import com.badlogic.gdx.Gdx
import xyz.upperlevel.hgame.DefaultFont

class Popup(val world: World, val id: Int, var text: String, var x: Float, var y: Float) {
    private val maxY = y + RAPTURE
    var fade = 0f

    fun show() {
        world.showPopup(text, x, y)
    }

    fun update() {
        y += RAPTURE_SPEED * Gdx.graphics.deltaTime
        if (y >= maxY) {
            hide()
        }
        fade = Math.max(maxY - y, 0f) / RAPTURE
    }

    fun renderHud(renderer: WorldRenderer.UIRenderer) {
        DefaultFont.DAMAGE_POPUP_FONT.color.a = fade
        renderer.drawWorldText(DefaultFont.DAMAGE_POPUP_FONT, text, x, y)
    }

    fun hide() {
        world.hidePopup(id)
    }

    companion object {
        const val RAPTURE = 0.75f
        const val RAPTURE_SPEED = 1.0f
    }
}
