package xyz.upperlevel.hgame.world.character

import xyz.upperlevel.hgame.network.Packet

// Every other action is managed trough Behaviour changes
// Unfortunately player jumping is done outside of the behaviour system
// so we have to sync it manually
data class PlayerJumpPacket(val entityId: Int) : Packet
