package xyz.upperlevel.hgame.screens.lobby.comp

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import xyz.upperlevel.hgame.world.player.PlayerEntityType

class UserPreviewComponent(var character: PlayerEntityType) : Image() {
    private var time: Float = 0f
    private var idleFrameIndex: Int = 0

    init {
        setFrame(0)
    }

    private fun setFrame(index: Int) {
        drawable = TextureRegionDrawable(character.idleRegions[index])
    }

    fun update(delta: Float) {
        time += delta
        if (time >= 0.2f) {
            time -= 0.2f
            idleFrameIndex = (idleFrameIndex + 1) % character.idleRegions.size
            setFrame(idleFrameIndex)
        }
    }
}
