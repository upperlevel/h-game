package xyz.upperlevel.hgame.world

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.ScreenViewport
import xyz.upperlevel.hgame.world.character.Actor
import xyz.upperlevel.hgame.world.scheduler.Task

object Conversation {
    private const val STAY = 20f

    private val font: BitmapFont

    private val stage = Stage(ScreenViewport())
    private var nameLabel: Label
    private var textLabel: Label

    private var playing: Sound? = null // the last sound played

    private val delay = object : Task() {
        override fun run() {
            hide()
        }
    }

    init {
        val generator = FreeTypeFontGenerator(Gdx.files.internal("font.ttf"))
        val parameter = FreeTypeFontGenerator.FreeTypeFontParameter()
        parameter.size = 32
        parameter.borderWidth = 2f
        parameter.borderColor = Color.BLACK

        font = generator.generateFont(parameter)
        generator.dispose()

        val table = Table()
        table.setFillParent(true)
        table.pad(10f)
        table.align(Align.top or Align.left)

        nameLabel = Label("", Label.LabelStyle(font, Color.RED))
        nameLabel.setWrap(false)

        textLabel = Label("", Label.LabelStyle(font, Color.YELLOW))
        textLabel.setWrap(true)

        table.add<Label>(nameLabel)
                .align(Align.left)
                .growX()
        table.row()
        table.add<Label>(textLabel)
                .align(Align.left)
                .growX()

        table.pack()
        stage.addActor(table)
    }

    fun create(name: String, text: String) {
        nameLabel.setText(name)
        textLabel.setText(text)
    }

    fun hide() {
        playing?.stop()
        playing = null

        // TODO: rude way to hide the conversation
        nameLabel.setText("")
        textLabel.setText("")
    }

    fun show(name: String, text: String, audio: String? = null) {
        playing?.stop()

        if (!delay.isCanceled) {
            delay.cancel()
        }
        create(name, text)
        delay.delay(STAY.toLong() * 1000)

        if (audio != null) { // todo: load audio globally
            try {
                playing = Gdx.audio.newSound(Gdx.files.internal("resources/audio/$audio"))
                playing!!.play(100f)
            } catch (e: Exception) {
                System.err.println("[WARNING] Audio file not found: $audio")
            }

        }
    }

    fun show(actor: Actor, text: String, audio: String? = null) {
        show(actor.character.name + ":", text, audio)
    }

    fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }

    fun render() {
        stage.act()
        stage.draw()
    }

    fun dispose() {
        stage.dispose()
        font.dispose()
    }
}
