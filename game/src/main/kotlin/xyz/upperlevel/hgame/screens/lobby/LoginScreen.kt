package xyz.upperlevel.hgame.screens.lobby

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.ScreenViewport
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import xyz.upperlevel.hgame.UI
import xyz.upperlevel.hgame.HGame
import xyz.upperlevel.hgame.matchmaking.LoginPacket
import xyz.upperlevel.hgame.matchmaking.MatchMakingCodec
import xyz.upperlevel.hgame.matchmaking.OperationResultPacket
import xyz.upperlevel.hgame.network.WebSocketClient
import xyz.upperlevel.hgame.runSync

class LoginScreen(val client: WebSocketClient) : ScreenAdapter() {
    private var stage: Stage = Stage(ScreenViewport())

    init {
        val table = Table()
        table.setFillParent(true)
        stage.addActor(table)

        val hint = Label("Insert your username:", UI.skin)
        hint.setAlignment(Align.center)
        table.add(hint).growX().row()

        val username = TextField("", UI.skin).apply {
            messageText = "Username"

            maxLength = 50
            setTextFieldFilter { _, char -> char in ACCEPTED_NAME_CHARS }

            setAlignment(Align.center)
        }
        table.add(username).padTop(25f).growX().row()

        val playButton = TextButton("Play", UI.skin).apply {
            isDisabled = true

            username.addListener(object : ChangeListener() {
                override fun changed(event: ChangeListener.ChangeEvent, actor: Actor) {
                    val empty = username.text.isEmpty()
                    isDisabled = empty
                }
            })
        }
        table.add(playButton).padTop(25f).width(175f).height(65f).row()

        val feedback = Label("", Label.LabelStyle().apply { font = UI.FONT_16 })
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
        UI.skin.dispose()
    }

    companion object {
        val ACCEPTED_NAME_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789@#<>!_-"
    }
}
