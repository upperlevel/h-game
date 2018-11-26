package xyz.upperlevel.hgame;

import xyz.upperlevel.hgame.input.TriggerInputActionPacket;
import xyz.upperlevel.hgame.network.Protocol;
import xyz.upperlevel.hgame.world.entity.EntitySpawnPacket;

public class GameProtocol {
    public static final int GAME_PORT = 23432;
    public static final Protocol PROTOCOL = Protocol.builder()
            .add(TriggerInputActionPacket.class)
            .add(EntitySpawnPacket.class)
            .build();
}
