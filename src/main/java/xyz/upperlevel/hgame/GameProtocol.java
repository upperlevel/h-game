package xyz.upperlevel.hgame;

import xyz.upperlevel.hgame.input.TriggerInputActionPacket;
import xyz.upperlevel.hgame.network.Protocol;

public class GameProtocol {
    public static final int GAME_PORT = 23432;
    public static final Protocol PROTOCOL = Protocol.builder()
            .add(TriggerInputActionPacket.class)
            .build();
}
