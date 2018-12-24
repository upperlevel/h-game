package xyz.upperlevel.hgame.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.List
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.viewport.ScreenViewport
import xyz.upperlevel.hgame.DefaultFont
import xyz.upperlevel.hgame.GameProtocol
import xyz.upperlevel.hgame.HGame
import xyz.upperlevel.hgame.event.EventHandler
import xyz.upperlevel.hgame.event.Listener
import xyz.upperlevel.hgame.network.Client
import xyz.upperlevel.hgame.network.Endpoint
import xyz.upperlevel.hgame.network.Server
import xyz.upperlevel.hgame.network.discovery.DiscoveryPairRequestEvent
import xyz.upperlevel.hgame.network.discovery.DiscoveryPairResponseEvent
import xyz.upperlevel.hgame.network.discovery.DiscoveryResponseEvent
import xyz.upperlevel.hgame.network.discovery.UdpDiscovery
import xyz.upperlevel.hgame.runSync
import java.io.IOException
import java.net.InetAddress
import java.util.*

class MatchMakingScreen(private val discovery: UdpDiscovery, private val name: String) : ScreenAdapter(), Listener {
    private val players = LinkedHashMap<InetAddress, String>()

    // Rendering
    private var stage: Stage
    private var skin: Skin
    private var renderPlayers: List<String>

    init {
        discovery.events.register(this)

        stage = Stage(ScreenViewport())

        // A skin can be loaded via JSON or defined programmatically, either is fine. Using a skin is optional but strongly
        // recommended solely for the convenience of getting a texture, region, etc as a drawable, tinted drawable, etc.
        skin = Skin()

        // Generate a 1x1 white texture and store it in the skin named "white".
        val pixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888)
        pixmap.setColor(Color.WHITE)
        pixmap.fill()
        skin.add("white", Texture(pixmap))

        skin.add("default", DefaultFont.FONT)

        val style = List.ListStyle()
        style.font = skin.getFont("default")
        style.down = skin.newDrawable("white", Color.BLUE)
        style.selection = skin.newDrawable("white", Color.SKY)
        skin.add("default", style)


        val table = Table(skin)
        table.setFillParent(true)

        renderPlayers = List(skin)
        renderPlayers.addListener(object : ChangeListener() {
            override fun changed(event: ChangeListener.ChangeEvent, actor: Actor) {
                if (!renderPlayers.selection.hasItems()) return
                val ip = players.keys
                        .stream()
                        .skip(renderPlayers.selectedIndex.toLong())
                        .findFirst()
                        .orElse(null) ?: return

                try {
                    discovery.askPairing(ip)
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        })
        renderPlayers.selection.required = false
        val scrollPane = ScrollPane(renderPlayers)
        table.add(scrollPane).grow()

        stage.addActor(table)
    }

    override fun show() {
        Gdx.input.inputProcessor = stage
        discovery.startService(name)
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

    override fun dispose() {
        discovery.events.unregister(this)
    }

    @EventHandler
    private fun onResponse(event: DiscoveryResponseEvent) {
        if (players.containsKey(event.ip)) return
        players[event.ip] = event.nickname
        val items = renderPlayers.items
        items.add(event.nickname)
        renderPlayers.setItems(items)
    }

    private fun onResult(opponentIp: InetAddress, opponentNick: String, isServer: Boolean) {
        runSync {
            val screen = GameScreen(name)
            HGame.get().screen = screen

            val ep: Endpoint

            if (isServer) {
                ep = Server(GameProtocol.PROTOCOL, GameProtocol.GAME_PORT)
            } else {
                ep = Client(GameProtocol.PROTOCOL, opponentIp, GameProtocol.GAME_PORT)
            }

            screen.connect(ep)
            ep.openAsync()
        }
    }

    @EventHandler
    private fun onPairRequest(event: DiscoveryPairRequestEvent) {
        discovery.stopService()
        onResult(event.ip, event.nickname, false)
    }

    @EventHandler
    private fun onPairResponse(event: DiscoveryPairResponseEvent) {
        discovery.stopService()
        onResult(event.ip, event.nickname, true)
    }
}
