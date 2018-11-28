package xyz.upperlevel.hgame.world.character.controllers

import com.badlogic.gdx.Gdx
import xyz.upperlevel.hgame.input.TriggerInputActionPacket
import xyz.upperlevel.hgame.network.Endpoint
import xyz.upperlevel.hgame.world.character.Actor
import xyz.upperlevel.hgame.world.sequence.Sequence

class RemoteController private constructor(actor: Actor, val endpoint: Endpoint) : Controller(actor) {
    private val test = Sequence.create()

    init {
        test.repeat({
            var moved = false
            for ((key, callback) in actions) {
                if (Gdx.input.isKeyPressed(key)) {
                    callback()
                    if (endpoint.isConnected) {
                        endpoint.send(TriggerInputActionPacket(actor.id, key))
                    }
                    moved = true
                }
            }

            if (!moved) {
                actor.control(0f, 0f)
            }
        }, 1).play()
    }

    override fun dismiss() {
        test.dismiss()
    }

    companion object {
        fun bind(player: Actor, endpoint: Endpoint): RemoteController = RemoteController(player, endpoint)
    }
}