package xyz.upperlevel.hgame.hud

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Scaling
import com.badlogic.gdx.utils.viewport.ScreenViewport
import org.apache.logging.log4j.LogManager
import xyz.upperlevel.hgame.DefaultFont
import xyz.upperlevel.hgame.world.World
import xyz.upperlevel.hgame.world.WorldRenderer
import xyz.upperlevel.hgame.world.entity.Player
import xyz.upperlevel.hgame.world.entity.EntitySpawnEvent

class HudRenderer(world: World) {
    private var stage: Stage = Stage(ScreenViewport())

    private val table: Table

    private val leftNameLabel: Label
    private val rightNameLabel: Label

    private val leftStats = PlayerStatsDrawer(false)
    private val rightStats = PlayerStatsDrawer(true)

    var leftPlayer: Player? = null
        set(value) {
            field = value
            leftStats.player = value
            leftNameLabel?.setText(value?.name ?: "")
        }
    var rightPlayer: Player? = null
        set(value) {
            field = value
            rightStats.player = value
            rightNameLabel?.setText(value?.name ?: "")
        }

    init {
        val skin = Skin().apply {
            add("default", Label.LabelStyle().apply {
                font = DefaultFont.FONT
            })
        }

        table = Table()
        table.setFillParent(true)
        table.align(Align.top)

        val height = DefaultFont.FONT.lineHeight
        val midWidth = Gdx.graphics.width / 2f

        leftNameLabel = Label("", skin)
        leftNameLabel.setAlignment(Align.topLeft)
        leftNameLabel.height = height
        leftNameLabel.setEllipsis(true)
        table.add(leftNameLabel).width(midWidth)

        rightNameLabel = Label("", skin)
        rightNameLabel.setAlignment(Align.topRight)
        rightNameLabel.height = height
        rightNameLabel.setEllipsis(true)
        table.add(rightNameLabel).width(midWidth).row()

        table.add(Image(leftStats, Scaling.fit, Align.topLeft)).growX().growY()
        table.add(Image(rightStats, Scaling.fit, Align.topRight)).growX().growY()
        table.row()

        stage.addActor(table)

        world.events.register(EntitySpawnEvent::class.java, { e ->
            if (e.entity !is Player) return@register
            onPlayerFind(e.entity)
        })
        world.entities.filter {it is Player }
                .forEach { onPlayerFind(it as Player) }
    }

    private fun onPlayerFind(p: Player) {
        if (!p.left) {
            leftPlayer = p
        } else {
            rightPlayer = p
        }
    }

    fun render(renderer: WorldRenderer) {
        // TODO: attach life and energy
        stage.act()
        stage.draw()
    }

    fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
        // Update table cells width
        table.cells.forEach { it.width(width / 2f) }
    }
}