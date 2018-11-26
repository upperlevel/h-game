package xyz.upperlevel.hgame.world.entity;

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

    /**
     * This is true only if the packet is a response to the client's request
     * When the client sends a request to the server the value is ignored
     */
    private boolean isConfirmation;
}
