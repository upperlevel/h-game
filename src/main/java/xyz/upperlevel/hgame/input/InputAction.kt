package xyz.upperlevel.hgame.input

import xyz.upperlevel.hgame.network.Endpoint
import xyz.upperlevel.hgame.world.character.Entity

typealias Consequence = (Entity) -> Unit

class InputAction(val entity: Entity, val id: Int, val trigger: InputTrigger, private val consequence : (Entity) -> Unit) {
    fun trigger(endpoint: Endpoint) {
        if (endpoint.isConnected) {
            endpoint.send(TriggerInputActionPacket(entity.id, id))
        }
        onTrigger()
    }

    fun onTrigger() {
        consequence(entity)
    }
}
