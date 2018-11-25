package xyz.upperlevel.hgame.scenario.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import xyz.upperlevel.hgame.network.Packet;
import xyz.upperlevel.hgame.network.ProtocolId;


@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@ProtocolId(0)
public class EntitySpawnPacket implements Packet {
    private int entityTypeId;
    private float x, y;
    private boolean isFacingLeft;
}
