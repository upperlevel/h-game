package xyz.upperlevel.hgame.screens.lobby

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.viewport.ScreenViewport
import xyz.upperlevel.hgame.DefaultFont
import xyz.upperlevel.hgame.screens.lobby.comp.GuestComponent
import xyz.upperlevel.hgame.screens.lobby.comp.PlayerComponent
import xyz.upperlevel.hgame.screens.lobby.comp.UserComponent
import xyz.upperlevel.hgame.world.entity.EntityTypes
import java.util.*
import kotlin.streams.asSequence

class LobbyScreen(val player: User, private val guests: MutableSet<User> = HashSet()) : ScreenAdapter() {
    private val skin: Skin = Skin()
    private val stage: Stage = Stage(ScreenViewport())

    private lateinit var playersTable: Table

    private val usersComponents: MutableMap<User, UserComponent> = HashMap()
    private val playerComponent: PlayerComponent
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

        val comp = GuestComponent(guest, skin)
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

        Table().also { container ->
            container.setFillParent(true)

            playersTable = Table().also { players ->
                val component =  PlayerComponent(player, skin)
                usersComponents[player] = component
                players.add(component).bottom()

                container.add(players).row()
            }


            readyButton = TextButton("", skin).apply {
                container.add(this).width(175f).height(65f).padTop(25f).row()

                addListener(object : ChangeListener() {
                    override fun changed(event: ChangeListener.ChangeEvent, actor: Actor) {
                        val ready = !player.ready
                        playerComponent.setReady(ready)

                        if (isPressed) {
                            setText(if (ready) "Cancel" else getReadyText())
                        }
                    }
                })
                setText(getReadyText())
            }

            inviteButton = TextButton("Invite", skin).apply {
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
            component.update(Gdx.graphics.deltaTime)
        }

        stage.act(delta)
        stage.draw()
    }
}
