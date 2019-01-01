package xyz.upperlevel.hgame.screens.lobby.comp

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import xyz.upperlevel.hgame.UI
import xyz.upperlevel.hgame.screens.lobby.User

class PlayerComponent(user: User, skin: Skin) : UserComponent(user, skin) {
    init {
        nameLabel.style = Label.LabelStyle().apply {
            font = UI.FONT_32
            fontColor = Color.YELLOW
        }

        readyLabel.style = Label.LabelStyle().apply {
            font = UI.FONT_32
            fontColor = Color.GREEN
        }

        previewCell.size(250f, 250f)
    }
}
