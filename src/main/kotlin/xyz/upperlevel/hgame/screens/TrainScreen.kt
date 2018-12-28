package xyz.upperlevel.hgame.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.ScreenViewport
import xyz.upperlevel.hgame.DefaultFont
import xyz.upperlevel.hgame.network.DisconnectedEndpoint
import xyz.upperlevel.hgame.world.World
import xyz.upperlevel.hgame.world.WorldRenderer
import xyz.upperlevel.hgame.world.entity.EntityTypes


class TrainScreen(val username: String) : ScreenAdapter() {
    // UI
    private var stage: Stage = Stage(ScreenViewport())

    private var playerPosition: Label
    private var playerBehaviour: Label

    // Game
    private var endpoint = DisconnectedEndpoint()

    private var world: World? = null
    private var renderer: WorldRenderer? = null

    init {
        // Creates style
        val skin = Skin().apply {
            val pixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888)
            pixmap.setColor(Color.WHITE)
            pixmap.fill()

            add("white", Texture(pixmap))
            add("default", DefaultFont.FONT)
        }

        Label.LabelStyle().apply {
            font = skin.getFont("default")
            fontColor = Color.SKY
            skin.add("default", this)
        }

        val table = Table().apply {
            setFillParent(true)
            align(Align.top or Align.left)
            // debug = true
        }

        playerBehaviour = Label("" ,skin).apply {
            setFontScale(0.75f)
            table.add(this).growX().pad(10.0f).row()
        }

        playerPosition = Label("", skin).apply {
            setFontScale(0.75f)
            table.add(this).growX().pad(10.0f).row()
        }

        table.pack()
        stage.addActor(table)
    }

    override fun show() {
        Gdx.input.inputProcessor = stage

        // Fake endpoint. We don't need network for the training session.
        endpoint = DisconnectedEndpoint()

        world = World().also {
            it.initEndpoint(endpoint)
            it.spawnPlayer(username, EntityTypes.MIXTER)
        }

        renderer = WorldRenderer(world!!)
    }

    override fun hide() {
        renderer?.dispose()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
        renderer?.resize(width, height)
    }

    override fun render(delta: Float) {
        world.let {
            world?.update()

            playerPosition.setText("Position: %.2f %.2f".format(world?.player?.x, world?.player?.y))
            playerBehaviour.setText("Current behaviour: %s".format(world?.player?.behaviour?.toString()))

            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
            renderer?.render()
        }
        stage.act(delta)
        stage.draw()
    }
}
