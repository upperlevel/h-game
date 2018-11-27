package xyz.upperlevel.hgame.network.discovery;

import com.google.common.io.BaseEncoding;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.upperlevel.hgame.event.EventChannel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Optional;

public class UdpDiscovery {
    public static int DISCOVERY_SERVICE_INTERVAL_MILLISECONDS = 2000;
    public static final int DISCOVERY_PORT = 23432;
    public static final int MAGIC_ID = 4242;
    private static final InetAddress BROADCAST;

    private static final Logger logger = LogManager.getLogger();

    private DatagramSocket socket;

    @Getter
    private EventChannel events = new EventChannel();
    private Thread discoverService;

    @NonNull
    @Getter
    @Setter
    private String nick = "ulisse";

    @Getter
    private boolean available = false;

    static {
        try {
            BROADCAST = InetAddress.getByName("255.255.255.255");
        } catch (UnknownHostException e) {
            throw new IllegalStateException(e);
        }
    }

    public void start() throws IOException {
        socket = new DatagramSocket(null);
        socket.setReuseAddress(true);
        socket.setBroadcast(true);
        socket.bind(new InetSocketAddress(DISCOVERY_PORT));
        Thread thread = new Thread(this::listen, "Discovery Server");
        thread.setDaemon(true);
        thread.start();
    }

    public void askPairing(InetAddress ip) throws IOException {
        reply(new InetSocketAddress(ip, DISCOVERY_PORT), PacketType.REQUEST_PAIR);
    }

    private static boolean isAddrSelf(InetAddress addr) throws SocketException {
        Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
        for (NetworkInterface netint : Collections.list(nets)) {
            for (InetAddress inet : Collections.list(netint.getInetAddresses())) {
                if (addr.equals(inet)) return true;
            }
        }
        return false;
    }

    public void listen() {
        byte[] buffer = new byte[2048];

        logger.info("Started UDP server");

        try {
            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                if (isAddrSelf(packet.getAddress())) continue;
                onPacketRead(packet);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error while listening to UDP packets", e);
        }
    }

    private void reply(SocketAddress target, PacketType type) throws IOException {
        logger.trace("Replying to: %s, event: %s", target, type);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] magicData = ByteBuffer.allocate(Integer.BYTES).putInt(MAGIC_ID).array();

        out.write(magicData, 0, magicData.length);
        out.write(type.toId());

        switch (type) {
            case RESPONSE_HELLO:
            case RESPONSE_CONFIRM:
            case RESPONSE_DENY:
            case REQUEST_PAIR:
                writeString(out, nick);
                break;
            default:
                throw new IllegalArgumentException("type " + type);
        }

        DatagramPacket packet = new DatagramPacket(out.toByteArray(), out.size(), target);
        socket.send(packet);
    }

    private void onPacketRead(DatagramPacket packet) throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap(packet.getData(), packet.getOffset(), packet.getLength());

        if (logger.isDebugEnabled()) {
            logger.debug("Packet: [{}]", BaseEncoding.base16().encode(packet.getData(), packet.getOffset(), packet.getLength()));
        }

        if (buffer.remaining() < Integer.BYTES || buffer.getInt() != MAGIC_ID) {
            logger.debug("Packet dropped: invalid magic id");
            return;
        }

        Optional<UdpDiscovery.PacketType> type = PacketType.fromId(buffer.get());
        if (!type.isPresent()) {
            logger.debug("Packet dropped: invalid event type");
            return;
        }

        InetSocketAddress sender = (InetSocketAddress) packet.getSocketAddress();

        String name;

        logger.debug("Packet from: {}, event: {}", sender, type.get());

        switch (type.get()) {
            case REQUEST_DISCOVERY:
                if (available) {
                    reply(sender, PacketType.RESPONSE_HELLO);
                }
                break;
            case REQUEST_PAIR:
                boolean pairSuccess = available;

                if (pairSuccess) {
                    name = readString(buffer);
                    pairSuccess = events.call(new DiscoveryPairRequestEvent(packet.getAddress(), name));
                }

                if (pairSuccess) {
                    reply(sender, PacketType.RESPONSE_CONFIRM);
                } else {
                    reply(sender, PacketType.RESPONSE_DENY);
                }
                break;
            case RESPONSE_HELLO:
                name = readString(buffer);
                events.call(new DiscoveryResponseEvent(sender.getAddress(), name));
                break;
            case RESPONSE_CONFIRM:
            case RESPONSE_DENY:
                name = readString(buffer);
                events.call(new DiscoveryPairResponseEvent(sender.getAddress(), name, type.get() == PacketType.RESPONSE_CONFIRM));
                break;
            default:
                throw new IllegalStateException();
        }
    }

    public void discover() throws IOException {
        logger.debug("Sending discovery packet");

        byte[] data = ByteBuffer.allocate(Integer.BYTES + 1)
                .putInt(MAGIC_ID)
                .put(PacketType.REQUEST_DISCOVERY.toId())
                .array();

        DatagramPacket packet = new DatagramPacket(data, data.length, new InetSocketAddress(BROADCAST, DISCOVERY_PORT));
        socket.send(packet);
    }

    private void serviceRunner() {
        logger.debug("Started discovery service");
        while (!Thread.interrupted()) {
            try {
                discover();
            } catch (IOException e) {
                // TODO: log
                e.printStackTrace();
            }
            try {
                Thread.sleep(DISCOVERY_SERVICE_INTERVAL_MILLISECONDS);
            } catch (InterruptedException e) {
                break;
            }
        }
        logger.debug("Stopped discovery service");
    }

    public void stopService() {
        if (!available) return;
        available = false;
        discoverService.interrupt();
        discoverService = null;
    }

    public void startService(@NonNull String nick) {
        this.nick = nick;
        available = true;
        discoverService = new Thread(this::serviceRunner, "Discovery Client");
        discoverService.setDaemon(true);
        discoverService.start();
    }

    private static String readString(ByteBuffer buffer) {
        byte len = buffer.get();
        byte[] strData = new byte[len];
        buffer.get(strData);
        return new String(strData, StandardCharsets.UTF_8);
    }

    private static void writeString(ByteArrayOutputStream out, @NonNull String str) throws IOException {
        out.write((byte)str.length());
        out.write(str.getBytes(StandardCharsets.UTF_8));
    }

    public enum PacketType {
        REQUEST_DISCOVERY,
        REQUEST_PAIR,
        RESPONSE_HELLO,
        RESPONSE_CONFIRM,
        RESPONSE_DENY;

        private static final PacketType[] TYPES_BY_ID = PacketType.values();

        public byte toId() {
            return (byte) this.ordinal();
        }

        public static Optional<PacketType> fromId(byte data) {
            int i = data & 0xFF;
            if (i < TYPES_BY_ID.length) return Optional.of(TYPES_BY_ID[i]);
            else return Optional.empty();
        }
    }
}
