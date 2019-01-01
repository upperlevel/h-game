package xyz.upperlevel.hgame

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.TextField


object UI {
    val DAMAGE_POPUP_FONT: BitmapFont
    val PLAYER_NAME_FONT: BitmapFont

    val FONT_32: BitmapFont
    val FONT_24: BitmapFont
    val FONT_16: BitmapFont
    val FONT_12: BitmapFont
    val FONT_8:  BitmapFont

    val skin: Skin = Skin()

    private fun initSkin() {
        skin.add("default", UI.FONT_32)

        val pixel = Pixmap(1, 1, Pixmap.Format.RGBA8888)
        pixel.setColor(Color.WHITE)
        pixel.fill()
        skin.add("white", Texture(pixel))

        skin.add("default", Label.LabelStyle().apply {
            font = skin.getFont("default")
        })

        skin.add("default", TextButton.TextButtonStyle().apply {
            font = skin.getFont("default")
            fontColor = Color.WHITE

            up = skin.newDrawable("white", Color.GRAY)
            down = skin.newDrawable("white", Color.GRAY)
            over = skin.newDrawable("white", Color.LIGHT_GRAY)
            disabled = skin.newDrawable("white", Color.DARK_GRAY)
        })

        skin.add("default", TextField.TextFieldStyle().apply {
            font = skin.getFont("default")
            fontColor = Color.SKY
            cursor = skin.newDrawable("white", Color.GRAY)
        })
    }

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
        parameter.borderWidth = 2.7f
        parameter.borderColor = Color.BLACK
        FONT_32 = generator.generateFont(parameter)

        parameter.size = 24
        parameter.color = Color.WHITE
        parameter.borderWidth = 2f
        parameter.borderColor = Color.BLACK
        FONT_24 = generator.generateFont(parameter)

        parameter.size = 16
        parameter.color = Color.WHITE
        parameter.borderWidth = 1f
        parameter.borderColor = Color.BLACK
        FONT_16 = generator.generateFont(parameter)

        parameter.size = 12
        parameter.color = Color.WHITE
        parameter.borderWidth = 0.7f
        parameter.borderColor = Color.BLACK
        FONT_12 = generator.generateFont(parameter)

        parameter.size = 8
        parameter.color = Color.WHITE
        parameter.borderWidth = 0.5f
        parameter.borderColor = Color.BLACK
        FONT_8 = generator.generateFont(parameter)

        initSkin()
    }
}
