package xyz.upperlevel.hgame.network

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.netty.channel.Channel
import io.netty.channel.EventLoopGroup
import xyz.upperlevel.hgame.event.EventChannel
import xyz.upperlevel.hgame.network.events.ConnectionCloseEvent
import xyz.upperlevel.hgame.network.events.ConnectionOpenEvent

abstract class Endpoint(val protocol: Protocol,
                        val side: NetSide) {
    val jsonMapper: ObjectMapper = jacksonObjectMapper()
    val events = EventChannel()

    var channel: Channel? = null
        private set

    protected var eventGroup: EventLoopGroup? = null

    val isConnected: Boolean
        get() = channel != null

    init {
        val module = SimpleModule()
        module.addDeserializer(PayloadPacket::class.java, PayloadPacketDeserializer(protocol))

        jsonMapper.registerModule(module)
    }


    open fun onPacketReceive(msg: PayloadPacket) {
        events.call(msg.data)
    }

    open fun onOpen(channel: Channel) {
        this.channel = channel
        events.call(ConnectionOpenEvent(channel))
    }

    open fun onClose() {
        events.call(ConnectionCloseEvent(channel!!))
        this.channel = null
    }

    open fun send(packet: Packet) {
        val id = protocol.fromClass(packet.javaClass)
                ?: throw IllegalArgumentException("Packet not registered: " + packet.javaClass)
        channel!!.writeAndFlush(PayloadPacket(id, packet))
    }

    abstract fun openAsync()

    open fun close() {
        if (!isConnected) return
        channel?.close()!!.sync()
    }

    fun closeGracefully() {
        close()
        eventGroup?.shutdownGracefully()
    }
}
