package xyz.upperlevel.hgame.input

import xyz.upperlevel.hgame.network.Endpoint
import xyz.upperlevel.hgame.world.character.Actor

typealias Consequence = (Actor) -> Unit

class InputAction(val actor: Actor, val id: Int, val trigger: InputTrigger, private val consequence : (Actor) -> Unit) {
    fun trigger(endpoint: Endpoint) {
        if (endpoint.isConnected) {
            endpoint.send(TriggerInputActionPacket(actor.id, id))
        }
        onTrigger()
    }

    fun onTrigger() {
        consequence(actor)
    }
}
