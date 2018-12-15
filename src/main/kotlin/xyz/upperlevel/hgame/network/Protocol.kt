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

        fun add(clazz: Class<out Packet>): ProtocolBuilder {
            val ann = clazz.getAnnotation(ProtocolId::class.java)
                    ?: throw IllegalArgumentException("Cannot find @ProtocolId for class: " + clazz.name)

            val previous = handle.putIfAbsent(ann.value, clazz)
            if (previous != null) {
                throw IllegalStateException("Id " + ann.value + " used for both " + previous.name + " and " + clazz.name)
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
