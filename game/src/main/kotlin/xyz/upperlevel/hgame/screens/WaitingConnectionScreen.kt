package xyz.upperlevel.hgame.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.ScreenViewport
import org.apache.logging.log4j.LogManager
import xyz.upperlevel.hgame.UI
import xyz.upperlevel.hgame.HGame
import xyz.upperlevel.hgame.event.EventListener
import xyz.upperlevel.hgame.network.Endpoint
import xyz.upperlevel.hgame.network.events.ConnectionOpenEvent
import xyz.upperlevel.hgame.runSync


// Wait for connection (both client and server) and add a "cancel" button that returns to the SelectHostScene
class WaitingConnectionScreen(
        private val endpoint: Endpoint,
        private val nextScreen: Screen) : ScreenAdapter() {

    private var stage: Stage = Stage(ScreenViewport())
    private var skin: Skin = Skin()
    private var text: Label

    private var points = 0
    private var gdelta = 0.0f

    init {
        // Generate a 1x1 white texture and store it in the skin named "white".
        val pixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888)
        pixmap.setColor(Color.WHITE)
        pixmap.fill()
        skin.add("white", Texture(pixmap))

        skin.add("default", UI.FONT_32)

        val labelStyle = Label.LabelStyle()
        labelStyle.font = skin.getFont("default")
        labelStyle.background = skin.newDrawable("white", Color.CLEAR)
        skin.add("default", labelStyle)


        val textButtonStyle = TextButton.TextButtonStyle()
        textButtonStyle.up = skin.newDrawable("white", Color.DARK_GRAY)
        textButtonStyle.down = skin.newDrawable("white", Color.RED)
        textButtonStyle.over = skin.newDrawable("white", Color.LIGHT_GRAY)
        textButtonStyle.font = skin.getFont("default")
        skin.add("default", textButtonStyle)


        val table = Table(skin)
        table.setFillParent(true)

        text = Label("Connecting", skin)
        text.setAlignment(Align.center)

        table.add<Label>(text).growX().row()

        val button = TextButton("Cancel", skin)
        button.addListener(object : ChangeListener() {
            override fun changed(event: ChangeListener.ChangeEvent, actor: Actor) {
                logger.warn("CANCEL")
            }
        })

        table.add(button).row()

        stage.addActor(table)

        endpoint.events.register(EventListener.listener(ConnectionOpenEvent::class.java, { runSync { HGame.get().screen = nextScreen } }))
    }

    override fun show() {
        Gdx.input.inputProcessor = stage
    }

    override fun render(delta: Float) {
        gdelta += delta

        if (gdelta >= BLINK_DELTA) {
            gdelta -= BLINK_DELTA
            points = (points + 1) % 4
            val str = "Connecting..."
            text.setText(str.substring(0, str.length - (3 - points)))
        }

        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        stage.act(delta)
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }

    companion object {
        private val logger = LogManager.getLogger()
        private const val BLINK_DELTA = 1.0f
    }
}
