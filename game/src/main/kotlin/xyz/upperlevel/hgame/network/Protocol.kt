package xyz.upperlevel.hgame.network

import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap

class Protocol(private val handle: BiMap<Int, Class<out Packet>>) {
    fun fromId(id: Int): Class<out Packet>? {
        return handle[id]
    }

    fun fromClass(clazz: Class<out Packet>): Int? {
        return handle.inverse()[clazz]
    }

    class ProtocolBuilder {
        private val handle = HashBiMap.create<Int, Class<out Packet>>()
        private var nextId = 0

        private fun findNextId(): Int {
            while (nextId in handle) nextId++
            return nextId++
        }

        fun add(clazz: Class<out Packet>, id: Int = -1): ProtocolBuilder {
            val typeId = if (id == -1) findNextId() else id

            val previous = handle.putIfAbsent(typeId, clazz)
            if (previous != null) {
                throw IllegalStateException("Id " + typeId + " used for both " + previous.name + " and " + clazz.name)
            }
            return this
        }

        fun build(): Protocol {
            return Protocol(handle)
        }
    }

    companion object {
        val EMPTY = Protocol.builder().build()

        fun builder(): ProtocolBuilder {
            return ProtocolBuilder()
        }
    }
}
