package xyz.upperlevel.hgame.world

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import xyz.upperlevel.hgame.world.character.Entity
import xyz.upperlevel.hgame.world.character.Player

class WorldRenderer {
    val camera: OrthographicCamera = OrthographicCamera()
    val spriteBatch: SpriteBatch = SpriteBatch()
    val shapeRenderer: ShapeRenderer = ShapeRenderer()


    fun render(world: World) {
        if (!world.isReady) return
        val height = world.height
        val groundHeight = world.groundHeight
        val player = world.player!!

        camera.setToOrtho(false, Gdx.graphics.width / Gdx.graphics.height.toFloat() * height, height)

        // Moves the camera to the position of the player.
        camera.position.x = player.x + Player.WIDTH / 2f
        camera.position.y = height / 2f + (player.y - groundHeight)
        camera.update()

        // Applies the camera to both the renderers.
        spriteBatch.projectionMatrix = camera.combined
        shapeRenderer.projectionMatrix = camera.combined

        // Draws the ground before all.
        shapeRenderer.color = Color.BLACK
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.rect(player.x - 10, 0f, 20f, groundHeight + 1)
        shapeRenderer.end()

        spriteBatch.begin()
        world.entities.forEach { actor -> actor.render(this) }
        spriteBatch.end()
    }

    fun dispose() {
        spriteBatch.dispose()
        shapeRenderer.dispose()
    }
}
