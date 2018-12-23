package xyz.upperlevel.hgame.world

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import xyz.upperlevel.hgame.hud.HudRenderer
import xyz.upperlevel.hgame.world.character.Player

class WorldRenderer(val world: World) {
    val camera: OrthographicCamera = OrthographicCamera()
    val spriteBatch: SpriteBatch = SpriteBatch()
    val hudRenderer: HudRenderer = HudRenderer(world)
    val shapeRenderer: ShapeRenderer = ShapeRenderer()
    val debugRenderer = Box2DDebugRenderer()


    fun render() {
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
        shapeRenderer.rect(player.x - 10, -groundHeight, 20f, groundHeight + 0.5f)
        shapeRenderer.end()

        spriteBatch.begin()
        world.entities.forEach { actor -> actor.render(this) }
        spriteBatch.end()

        debugRenderer.render(world.physics, camera.combined)

        hudRenderer.render(this)
    }

    fun dispose() {
        spriteBatch.dispose()
        shapeRenderer.dispose()
    }

    fun resize(width: Int, height: Int) {
        hudRenderer.resize(width, height)
    }
}
