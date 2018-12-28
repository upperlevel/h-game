package xyz.upperlevel.hgame.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.ScreenViewport
import org.apache.logging.log4j.LogManager
import xyz.upperlevel.hgame.DefaultFont
import xyz.upperlevel.hgame.HGame
import xyz.upperlevel.hgame.event.EventListener
import xyz.upperlevel.hgame.network.Endpoint
import xyz.upperlevel.hgame.network.events.ConnectionOpenEvent
import xyz.upperlevel.hgame.runSync
import xyz.upperlevel.hgame.world.World
import xyz.upperlevel.hgame.world.entity.EntityType
import xyz.upperlevel.hgame.world.entity.EntityTypes
import xyz.upperlevel.hgame.world.player.PlayerEntityType

class CharacterChoiceScreen(
        val username: String,
        val world: World,
        private val nextScreen: Screen) : ScreenAdapter() {

    private var stage: Stage = Stage(ScreenViewport())
    private var skin: Skin = Skin()

    private var charNameLabel: Label

    // Preview data
    private val previewScale = 3f
    private var charPreview: Image
    private var charPreviewCell: Cell<Image>
    private var gdelta = 0f
    private var regionIndex = 0

    var characters = EntityTypes.playable

    private var currentPlayer: PlayerEntityType = characters[0]
    private var currentRegion: TextureRegion? = null
    private var currentName: String? = null

    var charId = 0
        set(value) {
            field = ((value % characters.size) + characters.size) % characters.size
            currentPlayer = characters[field]
        }

    init {
        // Generate a 1x1 white texture and store it in the skin named "white".
        val pixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888)
        pixmap.setColor(Color.WHITE)
        pixmap.fill()
        skin.add("white", Texture(pixmap))

        skin.add("default", DefaultFont.FONT)

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

        Label("Select Character", skin).apply {
            setAlignment(Align.center)
            table.add(this).growX().row()
        }

        charNameLabel = Label("Ulisse", skin).apply {
            setAlignment(Align.center)
            table.add(this).growX().row()
        }
        Table(skin).apply {
            val hgroup = this
            TextButton("<", skin).apply {
                addListener(object : ChangeListener() {
                    override fun changed(event: ChangeListener.ChangeEvent, actor: Actor) {
                        charId--
                    }
                })
                hgroup.add(this).fillY()
            }
            charPreview = Image().apply {
                setAlign(Align.center)
                charPreviewCell = hgroup.add(this)
            }

            TextButton(">", skin).apply {
                addListener(object : ChangeListener() {
                    override fun changed(event: ChangeListener.ChangeEvent, actor: Actor) {
                        charId++
                    }
                })
                hgroup.add(this).fillY()
            }
            align(Align.center)
            table.add(this).row()
        }


        TextButton("Ok", skin).apply {
            addListener(object : ChangeListener() {
                override fun changed(event: ChangeListener.ChangeEvent, actor: Actor) {
                    world.spawnPlayer(username, currentPlayer)
                    HGame.get().screen = nextScreen
                }
            })

            table.add(this).row()
        }

        stage.addActor(table)
    }

    override fun show() {
        Gdx.input.inputProcessor = stage
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        gdelta += delta
        if (gdelta >= 0.2f) {
            gdelta -= 0.2f
            regionIndex = (regionIndex + 1) % currentPlayer.idleRegions.size
        }

        val realRegion = currentPlayer.idleRegions[regionIndex]
        if (currentRegion != realRegion) {
            currentRegion = realRegion
            charPreviewCell.size(
                    realRegion.regionWidth.toFloat() * previewScale,
                    realRegion.regionHeight.toFloat() * previewScale
            )
            charPreview.drawable = TextureRegionDrawable(realRegion)
        }

        val realName = currentPlayer.id
        if (currentName != realName) {
            currentName = realName
            charNameLabel.setText(realName)
        }

        stage.act(delta)
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }

    companion object {
        private val logger = LogManager.getLogger()
    }
}