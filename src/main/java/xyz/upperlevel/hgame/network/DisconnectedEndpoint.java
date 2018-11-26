package xyz.upperlevel.hgame.network;

public class DisconnectedEndpoint extends Endpoint {
    public DisconnectedEndpoint() {
        super(null, NetSide.MASTER);
    }

    @Override
    public void send(Packet packet) {
    }

    @Override
    public void openAsync() {
    }

    @Override
    public void close() {
    }
}
