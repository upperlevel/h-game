package xyz.upperlevel.hgame

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator


object DefaultFont {
    val FONT: BitmapFont

    init {
        val generator = FreeTypeFontGenerator(Gdx.files.internal("Lato.ttf"))
        val parameter = FreeTypeFontGenerator.FreeTypeFontParameter()
        parameter.size = 32
        parameter.borderWidth = 2f
        parameter.borderColor = Color.BLACK

        FONT = generator.generateFont(parameter)
    }
}
