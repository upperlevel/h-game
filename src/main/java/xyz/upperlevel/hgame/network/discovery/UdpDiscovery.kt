package xyz.upperlevel.hgame.network.discovery

import com.google.common.io.BaseEncoding
import lombok.NonNull
import org.apache.logging.log4j.LogManager
import xyz.upperlevel.hgame.event.EventChannel
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.*
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.util.*

class UdpDiscovery {
    private var socket: DatagramSocket = DatagramSocket(null)

    val events = EventChannel()
    private var discoverService: Thread? = null

    var nick = "ulisse"

    var isAvailable = false
        private set

    @Throws(IOException::class)
    fun start() {
        socket.reuseAddress = true
        socket.broadcast = true
        socket.bind(InetSocketAddress(DISCOVERY_PORT))
        val thread = Thread(this::listen, "Discovery Server")
        thread.isDaemon = true
        thread.start()
    }

    fun askPairing(ip: InetAddress) {
        reply(InetSocketAddress(ip, DISCOVERY_PORT), PacketType.REQUEST_PAIR)
    }

    fun listen() {
        val buffer = ByteArray(2048)

        logger.info("Started UDP server")

        try {
            while (true) {
                val packet = DatagramPacket(buffer, buffer.size)
                socket.receive(packet)

                if (isAddrSelf(packet.address)) continue
                onPacketRead(packet)
            }
        } catch (e: IOException) {
            throw RuntimeException("Error while listening to UDP packets", e)
        }

    }

    private fun reply(target: SocketAddress, type: PacketType) {
        logger.trace("Replying to: %s, event: %s", target, type)

        val out = ByteArrayOutputStream()
        val magicData = ByteBuffer.allocate(Integer.BYTES).putInt(MAGIC_ID).array()

        out.write(magicData, 0, magicData.size)
        out.write(type.toId().toInt())

        when (type) {
            UdpDiscovery.PacketType.RESPONSE_HELLO,
            UdpDiscovery.PacketType.RESPONSE_CONFIRM,
            UdpDiscovery.PacketType.RESPONSE_DENY,
            UdpDiscovery.PacketType.REQUEST_PAIR -> writeString(out, nick)
            else -> throw IllegalArgumentException("type $type")
        }

        val packet = DatagramPacket(out.toByteArray(), out.size(), target)
        socket.send(packet)
    }

    private fun onPacketRead(packet: DatagramPacket) {
        val buffer = ByteBuffer.wrap(packet.data, packet.offset, packet.length)

        if (logger.isDebugEnabled) {
            logger.debug("Packet: [{}]", BaseEncoding.base16().encode(packet.data, packet.offset, packet.length))
        }

        if (buffer.remaining() < Integer.BYTES || buffer.int != MAGIC_ID) {
            logger.debug("Packet dropped: invalid magic id")
            return
        }

        val type = PacketType.fromId(buffer.get())
        if (!type.isPresent) {
            logger.debug("Packet dropped: invalid event type")
            return
        }

        val sender = packet.socketAddress as InetSocketAddress

        val name: String

        logger.debug("Packet from: {}, event: {}", sender, type.get())

        when (type.get()) {
            UdpDiscovery.PacketType.REQUEST_DISCOVERY -> if (isAvailable) {
                reply(sender, PacketType.RESPONSE_HELLO)
            }
            UdpDiscovery.PacketType.REQUEST_PAIR -> {
                var pairSuccess = isAvailable

                if (pairSuccess) {
                    name = readString(buffer)
                    pairSuccess = events.call(DiscoveryPairRequestEvent(packet.address, name))
                }

                if (pairSuccess) {
                    reply(sender, PacketType.RESPONSE_CONFIRM)
                } else {
                    reply(sender, PacketType.RESPONSE_DENY)
                }
            }
            UdpDiscovery.PacketType.RESPONSE_HELLO -> {
                name = readString(buffer)
                events.call(DiscoveryResponseEvent(sender.address, name))
            }
            UdpDiscovery.PacketType.RESPONSE_CONFIRM, UdpDiscovery.PacketType.RESPONSE_DENY -> {
                name = readString(buffer)
                events.call(DiscoveryPairResponseEvent(sender.address, name, type.get() == PacketType.RESPONSE_CONFIRM))
            }
        }
    }

    @Throws(IOException::class)
    fun discover() {
        logger.debug("Sending discovery packet")

        val data = ByteBuffer.allocate(Integer.BYTES + 1)
                .putInt(MAGIC_ID)
                .put(PacketType.REQUEST_DISCOVERY.toId())
                .array()

        val packet = DatagramPacket(data, data.size, InetSocketAddress(BROADCAST, DISCOVERY_PORT))
        socket.send(packet)
    }

    private fun serviceRunner() {
        logger.debug("Started discovery service")
        while (!Thread.interrupted()) {
            try {
                discover()
            } catch (e: IOException) {
                // TODO: log
                e.printStackTrace()
            }

            try {
                Thread.sleep(DISCOVERY_SERVICE_INTERVAL_MILLISECONDS.toLong())
            } catch (e: InterruptedException) {
                break
            }

        }
        logger.debug("Stopped discovery service")
    }

    fun stopService() {
        if (!isAvailable) return
        isAvailable = false
        discoverService?.interrupt()
        discoverService = null
    }

    fun startService(nick: String) {
        this.nick = nick
        isAvailable = true
        discoverService = Thread(Runnable { this.serviceRunner() }, "Discovery Client")
        discoverService!!.isDaemon = true
        discoverService!!.start()
    }

    enum class PacketType {
        REQUEST_DISCOVERY,
        REQUEST_PAIR,
        RESPONSE_HELLO,
        RESPONSE_CONFIRM,
        RESPONSE_DENY;

        fun toId(): Byte {
            return this.ordinal.toByte()
        }

        companion object {

            private val TYPES_BY_ID = PacketType.values()

            fun fromId(data: Byte): Optional<PacketType> {
                val i = data.toInt() and 0xFF
                return if (i < TYPES_BY_ID.size)
                    Optional.of(TYPES_BY_ID[i])
                else
                    Optional.empty()
            }
        }
    }

    companion object {
        var DISCOVERY_SERVICE_INTERVAL_MILLISECONDS = 2000
        val DISCOVERY_PORT = 23432
        val MAGIC_ID = 4242
        private val BROADCAST: InetAddress

        private val logger = LogManager.getLogger()

        init {
            try {
                BROADCAST = InetAddress.getByName("255.255.255.255")
            } catch (e: UnknownHostException) {
                throw IllegalStateException(e)
            }

        }

        @Throws(SocketException::class)
        private fun isAddrSelf(addr: InetAddress): Boolean {
            val nets = NetworkInterface.getNetworkInterfaces()
            for (netint in Collections.list(nets)) {
                for (inet in Collections.list(netint.inetAddresses)) {
                    if (addr == inet) return true
                }
            }
            return false
        }

        private fun readString(buffer: ByteBuffer): String {
            val len = buffer.get().toInt()
            val strData = ByteArray(len)
            buffer.get(strData)
            return String(strData, StandardCharsets.UTF_8)
        }

        @Throws(IOException::class)
        private fun writeString(out: ByteArrayOutputStream, str: String) {
            out.write(str.length.toByte().toInt())
            out.write(str.toByteArray(StandardCharsets.UTF_8))
        }
    }
}
