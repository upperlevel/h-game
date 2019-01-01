package xyz.upperlevel.hgame.screens.lobby.comp

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import xyz.upperlevel.hgame.UI
import xyz.upperlevel.hgame.screens.lobby.User


class GuestComponent(user: User, skin: Skin) : UserComponent(user, skin) {
    init {
        nameLabel.style = Label.LabelStyle().apply {
            font = UI.FONT_16
            fontColor = Color.LIGHT_GRAY
        }

        readyLabel.style = Label.LabelStyle().apply {
            font = UI.FONT_16
            fontColor = Color.GREEN
        }

        previewComponentCell.size(175f, 175f)

        addListener(object : ClickListener() {
            override fun enter(event: InputEvent, x: Float, y: Float, pointer: Int, fromActor: Actor?) {
                previewComponent.color = Color.RED
            }

            override fun clicked(event: InputEvent, x: Float, y: Float) {
                // TODO kick the player
            }

            override fun exit(event: InputEvent, x: Float, y: Float, pointer: Int, fromActor: Actor?) {
                previewComponent.color = Color.WHITE
            }
        })
    }
}