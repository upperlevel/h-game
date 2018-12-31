package xyz.upperlevel.hgame.screens.lobby.comp

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import xyz.upperlevel.hgame.DefaultFont
import xyz.upperlevel.hgame.screens.lobby.User

class PlayerComponent(user: User, skin: Skin) : UserComponent(user, skin) {
    init {
        nameLabel.style = Label.LabelStyle().apply {
            font = DefaultFont.FONT
            fontColor = Color.YELLOW
        }

        readyLabel.style = Label.LabelStyle().apply {
            font = DefaultFont.FONT
            fontColor = Color.GREEN
        }

        previewComponentCell.size(250f, 250f)
    }
}
