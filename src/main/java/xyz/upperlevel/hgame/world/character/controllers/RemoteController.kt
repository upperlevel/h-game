package xyz.upperlevel.hgame.world.character.controllers

import com.badlogic.gdx.Gdx
import xyz.upperlevel.hgame.network.Endpoint
import xyz.upperlevel.hgame.world.character.Entity
import xyz.upperlevel.hgame.world.sequence.Sequence

class RemoteController private constructor(entity: Entity, val endpoint: Endpoint) : Controller(entity) {
    private val test = Sequence.create()

    init {
        test.repeat({
            var moved = false
            for ((key, callback) in actions) {
                if (Gdx.input.isKeyPressed(key)) {
                    callback()
                    if (endpoint.isConnected) {
                        endpoint.send(TriggerInputActionPacket(entity.id, key))
                    }
                    moved = true
                }
            }

            if (!moved) {
                entity.control(0f, 0f)
            }
        }, 1).play()
    }

    override fun dismiss() {
        test.dismiss()
    }

    companion object {
        fun bind(player: Entity, endpoint: Endpoint): RemoteController = RemoteController(player, endpoint)
    }
}