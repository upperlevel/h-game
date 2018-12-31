package xyz.upperlevel.hgame

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator


object DefaultFont {
    val DAMAGE_POPUP_FONT: BitmapFont
    val PLAYER_NAME_FONT: BitmapFont

    val FONT: BitmapFont
    val SMALL_FONT: BitmapFont

    init {
        val generator = FreeTypeFontGenerator(Gdx.files.internal("font.ttf"))
        val parameter = FreeTypeFontGenerator.FreeTypeFontParameter()

        parameter.size = 12
        parameter.color = Color.RED
        parameter.borderWidth = 1f
        parameter.borderColor = Color.BLACK
        DAMAGE_POPUP_FONT = generator.generateFont(parameter)

        parameter.size = 16
        parameter.color = Color.YELLOW
        parameter.borderWidth = 1f
        parameter.borderColor = Color.BLACK
        PLAYER_NAME_FONT = generator.generateFont(parameter)

        parameter.size = 32
        parameter.color = Color.WHITE
        parameter.borderWidth = 3f
        parameter.borderColor = Color.BLACK
        FONT = generator.generateFont(parameter)

        parameter.size = 16
        parameter.color = Color.WHITE
        parameter.borderWidth = 1f
        parameter.borderColor = Color.BLACK
        SMALL_FONT = generator.generateFont(parameter)
    }
}
