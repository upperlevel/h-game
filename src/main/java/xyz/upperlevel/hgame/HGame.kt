package xyz.upperlevel.hgame

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import org.apache.logging.log4j.core.config.Configurator
import xyz.upperlevel.hgame.network.discovery.UdpDiscovery
import xyz.upperlevel.hgame.screens.LoginScreen
import xyz.upperlevel.hgame.world.Event
import xyz.upperlevel.hgame.world.scheduler.Scheduler
import xyz.upperlevel.hgame.world.sequence.Sequence
import java.io.IOException

class HGame : Game() {

    var discovery = UdpDiscovery()

    override fun create() {
        instance = this

        Configurator.initialize("config", null, Gdx.files.internal("log4j2.xml").path())

        try {
            discovery.start()
        } catch (e: IOException) {
            throw RuntimeException(e)
        }

        setScreen(LoginScreen())
    }

    override fun render() {
        // Global APIs update
        Scheduler.update()
        Sequence.updateAll()
        Event.update()

        super.render()
    }

    companion object {
        private var instance: HGame? = null

        fun get(): HGame {
            return instance!!
        }

        @JvmStatic
        fun main(args: Array<String>) {
            val config = LwjglApplicationConfiguration()

            config.title = "H-Game"
            config.width = 720
            config.height = 720

            LwjglApplication(HGame(), config)
        }
    }
}
