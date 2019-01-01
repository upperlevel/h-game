package xyz.upperlevel.hgame.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.viewport.ScreenViewport
import xyz.upperlevel.hgame.UI
import xyz.upperlevel.hgame.GameProtocol
import xyz.upperlevel.hgame.HGame
import xyz.upperlevel.hgame.network.Client
import xyz.upperlevel.hgame.network.Endpoint
import xyz.upperlevel.hgame.network.Server

import java.net.InetAddress
import java.net.UnknownHostException

class SelectHostScene(val username: String) : ScreenAdapter() {
    // Rendering
    private var stage: Stage = Stage(ScreenViewport())
    private var skin: Skin = Skin()

    init {
        // Generate a 1x1 white texture and store it in the skin named "white".
        val pixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888)
        pixmap.setColor(Color.WHITE)
        pixmap.fill()
        skin.add("white", Texture(pixmap))

        skin.add("default", UI.FONT_32)

        // Configure a TextButtonStyle and name it "default". Skin resources are stored by type, so this doesn't overwrite the font.
        val textButtonStyle = TextButton.TextButtonStyle()
        textButtonStyle.up = skin.newDrawable("white", Color.DARK_GRAY)
        textButtonStyle.down = skin.newDrawable("white", Color.NAVY)
        textButtonStyle.disabled = skin.newDrawable("white", Color.RED)
        textButtonStyle.checked = skin.newDrawable("white", Color.SKY)
        textButtonStyle.over = skin.newDrawable("white", Color.LIGHT_GRAY)
        textButtonStyle.font = skin.getFont("default")
        skin.add("default", textButtonStyle)

        val textFieldStyle = TextFieldStyle()
        textFieldStyle.font = skin.getFont("default")
        textFieldStyle.fontColor = Color.SKY
        textFieldStyle.cursor = skin.newDrawable("white", Color.GRAY)

        skin.add("default", textFieldStyle)


        val table = Table(skin)
        table.setFillParent(true)

        val clientButton = TextButton("Client", skin)
        val serverButton = TextButton("Server", skin)

        val btnGroup = ButtonGroup(clientButton, serverButton)

        table.add(clientButton)
        table.add(serverButton)
        table.row()

        val ipField = TextField("", skin)
        ipField.messageText = "host"
        ipField.isDisabled = true

        table.add(ipField)

        val portField = TextField(Integer.toString(GameProtocol.GAME_PORT), skin)
        portField.messageText = "port"
        portField.setTextFieldFilter { _, c -> Character.isDigit(c) }

        table.add(portField).row()

        val connect = TextButton("Connect", skin)
        connect.isDisabled = true

        connect.addListener(object : ChangeListener() {
            override fun changed(event: ChangeListener.ChangeEvent, actor: Actor) {
                val endpoint: Endpoint

                endpoint = when {
                    clientButton.isChecked -> try {
                        Client(GameProtocol.PROTOCOL, InetAddress.getByName(ipField.text), GameProtocol.GAME_PORT)
                    } catch (e: UnknownHostException) {
                        e.printStackTrace()
                        return
                    }
                    serverButton.isChecked -> Server(GameProtocol.PROTOCOL, Integer.parseInt(portField.text))
                    else -> return
                }

                val game = GameScreen()
                //val charChoice = CharacterChoiceScreen(username, game.world, game)
                //val connScreen = WaitingConnectionScreen(endpoint, charChoice)

                game.connect(endpoint)

                endpoint.openAsync()

                //HGame.get().screen = connScreen
            }
        })

        table.add(connect).row()

        val onBtnChange = object : ChangeListener() {
            override fun changed(event: ChangeListener.ChangeEvent, actor: Actor) {
                ipField.isDisabled = serverButton.isChecked
                connect.isDisabled = btnGroup.checkedIndex == -1
            }
        }

        serverButton.addListener(onBtnChange)
        clientButton.addListener(onBtnChange)

        stage.addActor(table)
    }

    override fun show() {
        Gdx.input.inputProcessor = stage
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        stage.act(delta)
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }
}
