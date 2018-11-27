package xyz.upperlevel.hgame.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.GL20
import xyz.upperlevel.hgame.network.DisconnectedEndpoint
import xyz.upperlevel.hgame.world.World
import xyz.upperlevel.hgame.world.WorldRenderer

class TrainScreen : ScreenAdapter() {
    private var endpoint = DisconnectedEndpoint()

    private var world: World? = null
    private var renderer: WorldRenderer? = null

    override fun show() {
        // Fake endpoint. We don't need network for the training session.
        endpoint = DisconnectedEndpoint()

        world = World()
        world!!.initEndpoint(endpoint)
        world!!.onGameStart()

        renderer = WorldRenderer()
    }

    override fun hide() {
        renderer!!.dispose()
    }

    override fun render(delta: Float) {
        // Update
        world!!.update(endpoint)

        // Render
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        renderer!!.render(world!!)
    }
}
