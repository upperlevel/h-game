package xyz.upperlevel.hgame.world.entity

import xyz.upperlevel.hgame.event.Event
import xyz.upperlevel.hgame.world.character.Entity

data class EntitySpawnEvent(val entity: Entity) : Event