package xyz.upperlevel.hgame.network;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@ToString
public class PayloadPacket {
    @Getter
    private final int id;

    @Getter
    private final Packet data;
}
