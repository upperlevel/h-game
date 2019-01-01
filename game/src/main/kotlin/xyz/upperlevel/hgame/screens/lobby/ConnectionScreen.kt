package xyz.upperlevel.hgame.screens.lobby

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.ScreenViewport
import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame
import xyz.upperlevel.hgame.DefaultFont
import xyz.upperlevel.hgame.HGame
import xyz.upperlevel.hgame.network.WebSocketClient
import xyz.upperlevel.hgame.runSync
import java.net.InetAddress
import java.util.concurrent.CompletableFuture

class ConnectionScreen : ScreenAdapter() {
    private val stage: Stage = Stage(ScreenViewport())
    private val skin: Skin = Skin()

    private val client: WebSocketClient = WebSocketClient("ws://localhost:9080")

    init {
        // Generate a 1x1 white texture and store it in the skin named "white".
        val pixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888)
        pixmap.setColor(Color.WHITE)
        pixmap.fill()
        skin.add("white", Texture(pixmap))
        skin.add("default", DefaultFont.FONT)

        // Configure a TextButtonStyle and name it "default". Skin resources are stored by type, so this doesn't overwrite the font.
        val textButtonStyle = TextButton.TextButtonStyle()
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

        val textFieldStyle = TextField.TextFieldStyle()
        textFieldStyle.font = skin.getFont("default")
        textFieldStyle.fontColor = Color.SKY
        textFieldStyle.cursor = skin.newDrawable("white", Color.GRAY)

        skin.add("default", textFieldStyle)

        tryConnect()
    }

    private fun initInboundHandler(channel: Channel) {
        // When we connect we send the version/purpose packet.
        // After we've sent this packet, if the purpose is "matchmaking" (and it is), we should receive an "ok" response.
        // When we've received it, we can go on to the next screen.
        channel.pipeline()
                .addLast(object : SimpleChannelInboundHandler<TextWebSocketFrame>() {
                    override fun channelRead0(ctx: ChannelHandlerContext, msg: TextWebSocketFrame) {
                        if (msg.text() == "ok") {
                            ctx.pipeline().remove(this)
                            runSync { HGame.get().screen = LoginScreen(client) }
                        }
                    }
                })
    }

    private fun tryConnect() {
        stage.clear()
        stage.addActor(connectingTable)

        CompletableFuture.supplyAsync {
            client.runCatching {
                connectAndDoHandshake()
                initInboundHandler(channel)
                send(TextWebSocketFrame("version A0.1\nmatchmaking"))
            }.onFailure {
                runSync {
                    stage.clear()
                    stage.addActor(noConnectionTable)
                }
            }.onSuccess {
                runSync {
                    stage.clear()
                    stage.addActor(connectedTable)
                }
            }
        }
    }

    override fun show() {
        Gdx.input.inputProcessor = stage
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0.8f, 0.8f, 0.8f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        stage.act(delta)
        stage.draw()
    }

    companion object {
        // TODO: Currently the server address is set to localhost.
        val SERVER_ADDRESS = InetAddress.getByName("localhost")!!
        const val SERVER_PORT = 9080
    }

    val connectingTable: Table
        get() =
            Table().apply {
                setFillParent(true)
                add(
                        Label("Connecting...", this@ConnectionScreen.skin).apply {
                            setAlignment(Align.center)
                        }
                ).growX().row()
            }

    val noConnectionTable: Table
        get() =
            Table().apply {
                setFillParent(true)
                add(
                        Label("Can't connect to the server :(", this@ConnectionScreen.skin).apply {
                            color = Color.RED
                            setAlignment(Align.center)
                        }
                ).growX().row()

                add(
                        TextButton("Retry", this@ConnectionScreen.skin).apply {
                            color = Color.RED

                            addListener(object : ClickListener() {
                                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                                    tryConnect()
                                }
                            })
                        }
                ).padTop(25f).width(175f).height(65f).center().row()
            }

    val connectedTable: Table
        get() =
            Table().apply {
                setFillParent(true)
                add(
                        Label("Connected!", this@ConnectionScreen.skin)
                ).row()

                add(
                        Label("Doing app handshake...", this@ConnectionScreen.skin)
                ).row()
            }
}

