package xyz.upperlevel.hgame.network;

public class DisconnectedEndpoint extends Endpoint {
    public DisconnectedEndpoint() {
        super(null);
    }

    @Override
    public void send(Packet packet) {
    }

    @Override
    public void close() {
    }
}
