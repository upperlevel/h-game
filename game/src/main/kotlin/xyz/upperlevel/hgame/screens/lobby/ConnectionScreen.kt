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
import xyz.upperlevel.hgame.UI
import xyz.upperlevel.hgame.HGame
import xyz.upperlevel.hgame.network.WebSocketClient
import xyz.upperlevel.hgame.runSync
import java.net.InetAddress
import java.util.concurrent.CompletableFuture

class ConnectionScreen : ScreenAdapter() {
    private val stage: Stage = Stage(ScreenViewport())

    private val client: WebSocketClient = WebSocketClient("ws://localhost:9080")

    init {
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
                        Label("Connecting...", UI.skin).apply {
                            setAlignment(Align.center)
                        }
                ).growX().row()
            }

    val noConnectionTable: Table
        get() =
            Table().apply {
                setFillParent(true)
                add(
                        Label("Can't connect to the server :(", UI.skin).apply {
                            color = Color.RED
                            setAlignment(Align.center)
                        }
                ).growX().row()

                add(
                        TextButton("Retry", UI.skin).apply {
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
                        Label("Connected!", UI.skin)
                ).row()

                add(
                        Label("Doing app handshake...", UI.skin)
                ).row()
            }
}

