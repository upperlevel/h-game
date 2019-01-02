package xyz.upperlevel.hgame.screens.lobby

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.viewport.ScreenViewport
import xyz.upperlevel.hgame.HGame
import xyz.upperlevel.hgame.UI
import xyz.upperlevel.hgame.screens.lobby.comp.GuestComponent
import xyz.upperlevel.hgame.screens.lobby.comp.PlayerComponent
import xyz.upperlevel.hgame.screens.lobby.comp.UserComponent
import xyz.upperlevel.hgame.world.entity.EntityType
import xyz.upperlevel.hgame.world.entity.EntityTypes
import java.util.*
import kotlin.streams.asSequence

class LobbyScreen(private val player: User, private val guests: MutableSet<User> = HashSet()) : ScreenAdapter() {
    private val stage: Stage = Stage(ScreenViewport())

    private lateinit var playersTable: Table

    val usersComponents: MutableMap<User, UserComponent> = HashMap()
    val playerComponent: PlayerComponent
        get() = usersComponents[player]!! as PlayerComponent

    private lateinit var readyButton: TextButton
    private lateinit var inviteButton: TextButton

    private fun getReadyText(): String {
        return if (guests.size > 0) "Ready" else "Play"
    }

    private fun addGuest(guest: User) {
        if (guests.size >= 3) {
            throw IllegalStateException("A guest tried to join when max number of guests is reached.")
        }

        guests.add(guest)

        val comp = GuestComponent(guest, UI.skin)
        usersComponents[guest] = comp
        playersTable.add(comp).bottom()

        readyButton.setText(getReadyText())
    }

    private fun removeGuest(guest: User) {
        guests.remove(guest)
        usersComponents[guest]!!.remove()

        readyButton.setText(getReadyText())
    }

    fun kickGuest(guest: User) {
        removeGuest(guest)
        // TODO: send kick packet
    }

    init {
        Table().also { container ->
            container.setFillParent(true)

            playersTable = Table().also { players ->
                val playerComp =  PlayerComponent(player, UI.skin)
                usersComponents[player] = playerComp
                players.add(playerComp).bottom()
                playerComp.addListener(object : ClickListener() {
                    override fun clicked(event: InputEvent, x: Float, y: Float) {
                        HGame.get().screen = CharacterChoiceScreen(this@LobbyScreen)
                    }
                })

                container.add(players).row()
            }


            readyButton = TextButton("", UI.skin).apply {
                container.add(this).width(175f).height(65f).padTop(25f).row()

                addListener(object : ChangeListener() {
                    override fun changed(event: ChangeListener.ChangeEvent, actor: Actor) {
                        if (isPressed) {
                            val ready = !player.ready
                            playerComponent.ready = ready

                            setText(if (ready) "Cancel" else getReadyText())
                        }
                    }
                })
                setText(getReadyText())
            }

            inviteButton = TextButton("Invite", UI.skin).apply {
                container.add(this).width(175f).height(65f).padTop(25f).row()

                addListener(object : ChangeListener() {
                    override fun changed(event: ChangeListener.ChangeEvent, actor: Actor) {
                        val source = "abcdefghijklmnopqrstuvwxyz"
                        val s = Random().ints(8, 0, source.length)
                                .asSequence()
                                .map(source::get)
                                .joinToString("")

                        if (isPressed) {
                            addGuest(User(s, EntityTypes.MIXTER))
                        }
                    }
                })
            }

            stage.addActor(container)
        }
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }

    override fun show() {
        Gdx.input.inputProcessor = stage
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        for (component in usersComponents.values) {
            component.update(delta)
        }

        stage.act(delta)
        stage.draw()
    }
}
