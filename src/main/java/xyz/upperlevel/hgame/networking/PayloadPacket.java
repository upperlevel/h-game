package xyz.upperlevel.hgame.networking;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class PayloadPacket {
    @Getter
    private final int id;

    @Getter
    private final Packet data;
}
