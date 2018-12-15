package xyz.upperlevel.hgame.network

class DisconnectedEndpoint : Endpoint(Protocol.EMPTY, NetSide.MASTER) {
    override fun send(packet: Packet) {}

    override fun openAsync() {}

    override fun close() {}
}
