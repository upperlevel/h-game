package xyz.upperlevel.hgame.world

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import xyz.upperlevel.hgame.hud.HudRenderer

class WorldRenderer(val world: World) {
    val worldCamera: OrthographicCamera = OrthographicCamera()

    val spriteBatch: SpriteBatch = SpriteBatch()
    val hudRenderer: HudRenderer = HudRenderer(world)
    val shapeRenderer: ShapeRenderer = ShapeRenderer()
    val debugRenderer = Box2DDebugRenderer()
    val uiRenderer = UIRenderer()

    val worldHeight: Float get() = world.height
    val worldWidth: Float get() = Gdx.graphics.width / Gdx.graphics.height.toFloat() * worldHeight

    fun render() {
        if (!world.isReady) return

        val groundHeight = world.groundHeight
        val player = world.player!!

        worldCamera.setToOrtho(false, worldWidth, worldHeight)

        // The player is in the middle of the camera frustum.
        val camX = player.centerX
        val camY = worldHeight / 2f + (player.y - groundHeight)

        worldCamera.position.x = camX
        worldCamera.position.y = camY
        worldCamera.update()


        // Renders world elements (whose camera uses world units).
        spriteBatch.projectionMatrix = worldCamera.combined
        shapeRenderer.projectionMatrix = worldCamera.combined

        shapeRenderer.color = Color.BLACK
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.rect(player.x - 10, -groundHeight, 20f, groundHeight + 0.5f)
        shapeRenderer.end()

        spriteBatch.begin()
        world.entities.forEach { entity -> entity.render(this) }
        world.effects.forEach { effect -> effect.draw(spriteBatch, Gdx.graphics.deltaTime) } // TODO use the same spriteBatch?
        spriteBatch.end()

        debugRenderer.render(world.physics, worldCamera.combined)

        uiRenderer.render()

        hudRenderer.render(this)
    }

    fun dispose() {
        spriteBatch.dispose()
        shapeRenderer.dispose()
    }

    fun resize(width: Int, height: Int) {
        hudRenderer.resize(width, height)
    }

    inner class UIRenderer {
        val camera: OrthographicCamera = OrthographicCamera()

        val viewportWidth: Float get() = Gdx.graphics.width.toFloat()
        val viewportHeight: Float get() = Gdx.graphics.height.toFloat()


        fun toScreenX(worldX: Float): Float {
            return worldX / worldWidth * Gdx.graphics.width.toFloat()
        }

        fun toScreenY(worldY: Float): Float {
            return worldY / worldHeight * Gdx.graphics.height.toFloat()
        }

        fun drawText(font: BitmapFont, text: String, x: Float, y: Float) {
            font.draw(spriteBatch, text, x, y + font.lineHeight)
        }

        fun drawWorldText(font: BitmapFont, text: String, worldX: Float, worldY: Float, centered: Boolean = false) {
            var screenX = toScreenX(worldX)
            if (centered) {
                var width = 0f
                for (char in text) {
                    width += font.data.getGlyph(char).width
                }
                screenX -= width / 2f
            }
            drawText(font, text, screenX, toScreenY(worldY))
        }

        fun render() {
            camera.setToOrtho(false, viewportWidth, viewportHeight)

            camera.position.x = toScreenX(worldCamera.position.x)
            camera.position.y = toScreenY(worldCamera.position.y)
            camera.update()

            spriteBatch.projectionMatrix = camera.combined
            spriteBatch.begin()

            world.popups.forEach { popup -> popup.renderHud(this) }
            world.entities.forEach { entity -> entity.renderHud(this) }

            spriteBatch.end()
        }
    }
}
