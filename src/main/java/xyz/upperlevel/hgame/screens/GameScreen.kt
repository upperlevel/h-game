package xyz.upperlevel.hgame.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.GL20
import xyz.upperlevel.hgame.network.Endpoint
import xyz.upperlevel.hgame.world.Conversation
import xyz.upperlevel.hgame.world.World
import xyz.upperlevel.hgame.world.WorldRenderer

class GameScreen : ScreenAdapter() {
    private var renderer: WorldRenderer? = null
    private val world = World()

    var endpoint: Endpoint? = null
        private set

    override fun show() {
        renderer = WorldRenderer()
    }

    override fun hide() {
        Conversation.dispose()
        renderer?.dispose()
    }

    fun connect(endpoint: Endpoint) {
        this.endpoint = endpoint
        world.initEndpoint(endpoint)
    }

    override fun resize(width: Int, height: Int) {
        Conversation.resize(width, height)
    }

    override fun render(delta: Float) {
        world.update(endpoint!!)

        // Render
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        renderer!!.render(world)
        Conversation.render()
    }
}
