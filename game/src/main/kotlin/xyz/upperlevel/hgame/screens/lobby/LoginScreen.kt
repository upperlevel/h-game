package xyz.upperlevel.hgame.screens.lobby

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.ScreenViewport
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import xyz.upperlevel.hgame.DefaultFont
import xyz.upperlevel.hgame.HGame
import xyz.upperlevel.hgame.matchmaking.LoginPacket
import xyz.upperlevel.hgame.matchmaking.MatchMakingCodec
import xyz.upperlevel.hgame.matchmaking.OperationResultPacket
import xyz.upperlevel.hgame.network.WebSocketClient
import xyz.upperlevel.hgame.runSync

class LoginScreen(val client: WebSocketClient) : ScreenAdapter() {
    private var stage: Stage = Stage(ScreenViewport())
    private var skin: Skin = Skin()

    init {

        // Generate a 1x1 white texture and store it in the skin named "white".
        val pixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888)
        pixmap.setColor(Color.WHITE)
        pixmap.fill()
        skin.add("white", Texture(pixmap))
        skin.add("default", DefaultFont.FONT)

        // Configure a TextButtonStyle and name it "default". Skin resources are stored by type, so this doesn't overwrite the font.
        val textButtonStyle = TextButtonStyle()
        textButtonStyle.up = skin.newDrawable("white", Color.DARK_GRAY)
        textButtonStyle.down = skin.newDrawable("white", Color.NAVY)
        textButtonStyle.disabled = skin.newDrawable("white", Color.RED)
        textButtonStyle.over = skin.newDrawable("white", Color.LIGHT_GRAY)
        textButtonStyle.font = skin.getFont("default")
        skin.add("default", textButtonStyle)

        val labelStyle = Label.LabelStyle()
        labelStyle.font = skin.getFont("default")
        labelStyle.background = skin.newDrawable("white", Color.CLEAR)
        skin.add("default", labelStyle)

        val textFieldStyle = TextFieldStyle()
        textFieldStyle.font = skin.getFont("default")
        textFieldStyle.fontColor = Color.SKY
        textFieldStyle.cursor = skin.newDrawable("white", Color.GRAY)

        skin.add("default", textFieldStyle)

        // Create a table that fills the screen. Everything else will go inside this table.
        val table = Table()
        table.setFillParent(true)
        stage.addActor(table)

        val hint = Label("Insert your username:", skin)
        hint.setAlignment(Align.center)
        table.add(hint).growX().row()

        val username = TextField("", skin).apply {
            messageText = "Username"

            maxLength = 50
            setTextFieldFilter { _, char -> char in ACCEPTED_NAME_CHARS }

            setAlignment(Align.center)
        }
        table.add(username).padTop(25f).growX().row()

        val playButton = TextButton("Play", skin).apply {
            isDisabled = true

            username.addListener(object : ChangeListener() {
                override fun changed(event: ChangeListener.ChangeEvent, actor: Actor) {
                    val empty = username.text.isEmpty()
                    isDisabled = empty
                }
            })
        }
        table.add(playButton).padTop(25f).width(175f).height(65f).row()

        val feedback = Label("", Label.LabelStyle().apply { font = DefaultFont.SMALL_FONT })
        feedback.setAlignment(Align.center)
        table.add(feedback).padTop(25f).growX().row()

        playButton.addListener(object : ChangeListener() {
            override fun changed(event: ChangeListener.ChangeEvent, actor: Actor) {
                if (playButton.isPressed) {
                    playButton.isDisabled = true
                    client.send(LoginPacket(username.text))

                    feedback.apply {
                        color = Color.CLEAR
                        feedback.setText("Packet sent...")
                    }
                }
            }
        })

        val pipe = client.channel.pipeline()
        pipe.addLast(MatchMakingCodec())
                .addLast(object : SimpleChannelInboundHandler<OperationResultPacket>() {
                    override fun channelRead0(ctx: ChannelHandlerContext, msg: OperationResultPacket) {
                        if (msg.error == null) {
                            pipe.remove(this)
                            runSync { HGame.get().screen = LobbyScreen(User(username.text)) }
                        } else {
                            playButton.isDisabled = false
                            feedback.apply {
                                color = Color.RED
                                feedback.setText(msg.error)
                            }
                        }
                    }
                })
    }

    override fun show() {
        Gdx.input.inputProcessor = stage
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        stage.act(Math.min(Gdx.graphics.deltaTime, 1 / 30f))
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }

    override fun dispose() {
        stage.dispose()
        skin.dispose()
    }

    companion object {
        val ACCEPTED_NAME_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789@#<>!_-"
    }
}
