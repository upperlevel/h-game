package xyz.upperlevel.hgame.network.discovery;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import xyz.upperlevel.hgame.event.EventChannel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class UdpDiscovery {
    public static int DISCOVERY_SERCVICE_INTERVAL_MILLISECONDS = 500;
    public static final int DISCOVERY_PORT = 23432;
    public static final int MAGIC_ID = 4242;
    private static final InetAddress BROADCAST;

    private DatagramSocket socket;
    @Getter
    private EventChannel events = new EventChannel();
    private Thread discoverService;

    @NonNull
    @Getter
    @Setter
    private String nick = "ulisse";

    @Getter
    @Setter
    private boolean available = false;


    static {
        try {
            BROADCAST = InetAddress.getByName("255.255.255.255");
        } catch (UnknownHostException e) {
            throw new IllegalStateException(e);
        }
    }

    public void start() throws IOException {
        socket = new DatagramSocket();
        socket.setReuseAddress(true);
        var thread = new Thread(this::listen, "Discovery Server");
        thread.setDaemon(true);
        thread.start();
    }

    public void listen() {
        byte[] buffer = new byte[2048];
        try {
            socket.bind(new InetSocketAddress(DISCOVERY_PORT));

            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, 0);
                socket.receive(packet);
                onPacketRead(packet);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error while listening to UDP packets", e);
        }
    }

    private void reply(SocketAddress target, PacketType type) throws IOException {
        var out = new ByteArrayOutputStream();
        out.write(type.toId());
        switch (type) {
            case RESPONSE_HELLO:
                out.write((byte)nick.length());
                out.write(nick.getBytes(StandardCharsets.UTF_8));
                break;
            case RESPONSE_CONFIRM:
            case RESPONSE_DENY:
                break;
            default:
                throw new IllegalArgumentException("type");
        }
        var packet = new DatagramPacket(out.toByteArray(), out.size(), target);
        socket.send(packet);
    }

    private static String readString(ByteBuffer buffer) {
        byte len = buffer.get();
        return new String(buffer.array(), buffer.arrayOffset(), len);
    }

    private void onPacketRead(DatagramPacket packet) throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap(packet.getData(), packet.getOffset(), packet.getLength());

        if (buffer.remaining() < Integer.BYTES) return;
        if (buffer.getInt() != MAGIC_ID) return;

        var type = PacketType.fromId(buffer.get());
        if (!type.isPresent()) return;

        var sender = (InetSocketAddress) packet.getSocketAddress();

        switch (type.get()) {
            case REQUEST_DISCOVERY:
                if (available) {
                    reply(sender, PacketType.RESPONSE_CONFIRM);
                }
                break;
            case REQUEST_PAIR:
                if (available) {
                    reply(sender, PacketType.RESPONSE_CONFIRM);
                } else {
                    reply(sender, PacketType.RESPONSE_DENY);
                }
                break;
            case RESPONSE_HELLO:
                var name = readString(buffer);
                events.call(new DiscoveryResponseEvent(sender.getAddress(), name));
                break;
            case RESPONSE_CONFIRM:
            case RESPONSE_DENY:
                events.call(new DiscoveryPairResponseEvent(sender.getAddress(), type.get() == PacketType.RESPONSE_CONFIRM));
                break;
            default:
                throw new IllegalStateException();
        }
    }

    public void discover() throws IOException {
        socket.setReuseAddress(true);
        socket.setBroadcast(true);

        byte[] data = new byte[] {
            PacketType.REQUEST_DISCOVERY.toId()
        };

        DatagramPacket packet = new DatagramPacket(data, data.length, new InetSocketAddress(BROADCAST, 9956));
        socket.send(packet);
    }

    private void serviceRunner() {
        while (!Thread.interrupted()) {
            try {
                discover();
            } catch (IOException e) {
                // TODO: log
                e.printStackTrace();
            }
            try {
                Thread.sleep(DISCOVERY_SERCVICE_INTERVAL_MILLISECONDS);
            } catch (InterruptedException ignored) {}
        }
    }

    public void stopService() {
        available = false;
        discoverService.interrupt();
    }

    public void startService(String nick) {
        this.nick = nick;
        available = true;
        discoverService = new Thread(this::serviceRunner, "Discovery Client");
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
