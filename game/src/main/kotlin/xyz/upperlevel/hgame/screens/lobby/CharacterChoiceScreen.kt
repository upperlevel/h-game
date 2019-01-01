package xyz.upperlevel.hgame.screens.lobby

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.ScreenViewport
import org.apache.logging.log4j.LogManager
import xyz.upperlevel.hgame.HGame
import xyz.upperlevel.hgame.UI
import xyz.upperlevel.hgame.screens.lobby.comp.CharacterPreview
import xyz.upperlevel.hgame.world.entity.EntityType
import xyz.upperlevel.hgame.world.entity.EntityTypes
import xyz.upperlevel.hgame.world.player.PlayerEntityType

class CharacterChoiceScreen(val caller: LobbyScreen) : ScreenAdapter() {
    private var stage: Stage = Stage(ScreenViewport())

    private lateinit var previewName: Label
    private lateinit var preview: CharacterPreview
    var selection = 0
        set(value) {
            val playable = EntityTypes.playable
            field = ((value % playable.size) + playable.size) % playable.size

            preview.character = playable[field]
            previewName.setText(playable[field].id)
        }

    fun getSelection(): PlayerEntityType {
        return EntityTypes.playable[selection]
    }


    init {
        val table = Table(UI.skin)
        table.setFillParent(true)

        stage.addActor(
                Table(UI.skin).also { container ->
                    container.setFillParent(true)

                    container.add(
                            Label("Select character:", UI.skin)
                    ).row()

                    container.add(
                            Table().also { character ->
                                previewName = Label("", UI.skin).apply {
                                    setAlignment(Align.center)
                                }
                                character.add(previewName).row()

                                character.add(
                                        Table().also { selector ->
                                            selector.add(
                                                    TextButton("<", UI.skin).apply {
                                                        addListener(object : ChangeListener() {
                                                            override fun changed(event: ChangeListener.ChangeEvent, actor: Actor) {
                                                                selection--
                                                            }
                                                        })
                                                    }.pad(10f)
                                            ).fillY()

                                            preview = CharacterPreview(EntityTypes.SANTY).apply {
                                                setAlign(Align.center)
                                            }
                                            selector.add(preview).size(250f, 250f)

                                            selector.add(
                                                    TextButton(">", UI.skin).apply {
                                                        addListener(object : ChangeListener() {
                                                            override fun changed(event: ChangeListener.ChangeEvent, actor: Actor) {
                                                                selection++
                                                            }
                                                        })
                                                    }.pad(10f)
                                            ).fillY()
                                        }
                                ).padTop(25f).row()
                            }
                    ).padTop(25f).row()

                    container.add(
                            TextButton("OK", UI.skin).apply {
                                addListener(object : ClickListener() {
                                    override fun clicked(event: InputEvent, x: Float, y: Float) {
                                        caller.playerComponent.setCharacter(getSelection())
                                        HGame.get().screen = caller
                                    }
                                })
                            }.pad(25f)
                    ).padTop(25f).row()
                }
        )
        selection = 0
    }

    override fun show() {
        Gdx.input.inputProcessor = stage
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        preview.update(delta)

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