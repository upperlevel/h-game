package xyz.upperlevel.hgame.screens.lobby.comp

import com.badlogic.gdx.scenes.scene2d.ui.Cell
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import xyz.upperlevel.hgame.screens.lobby.User
import xyz.upperlevel.hgame.world.entity.EntityType
import xyz.upperlevel.hgame.world.player.PlayerEntityType

open class UserComponent(val user: User, skin: Skin) : Table() {
    protected var nameLabel: Label = Label(user.name, skin)
    protected var readyLabel: Label = Label(getReadyText(), skin)

    protected var preview: CharacterPreview = CharacterPreview(user.entityType as PlayerEntityType)
    protected var previewCell: Cell<CharacterPreview>

    var ready: Boolean
        get() {
            return user.ready
        }
        set(value) {
            user.ready = value
            readyLabel.setText(getReadyText())
        }

    var character: EntityType
        get() {
            return user.entityType
        }

        set(value) {
            user.entityType = value
            preview.character = value as PlayerEntityType
        }

    init {
        this.add(nameLabel).row()
        this.add(readyLabel).row()
        previewCell = this.add(preview).also { it.row() }
    }

    private fun getReadyText(): String {
        return if (user.ready) "Ready" else ""
    }

    fun update(delta: Float) {
        preview.update(delta)
    }
}
