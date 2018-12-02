package xyz.upperlevel.hgame.world

import xyz.upperlevel.hgame.world.character.Entity
import java.util.*
import java.util.function.Supplier
import kotlin.collections.Map.Entry

object Event {
    private val events = ArrayList<Entry<Supplier<Boolean>, Runnable>>()

    fun watch(trigger: Supplier<Boolean>, action: Runnable) {
        events.add(AbstractMap.SimpleEntry(trigger, action))
    }

    fun update() {
        val eventsCopy = ArrayList(events)
        for (entry in eventsCopy) {
            if (entry.key.get()) {
                entry.value.run()
                events.remove(entry)
            }
        }
    }

    fun greaterX(entity: Entity, x: Float): () ->  Boolean {
        return { entity.x >= x }
    }

    fun lowerX(entity: Entity, x: Float): () -> Boolean {
        return { entity.x <= x }
    }
}
