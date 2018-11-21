package xyz.upperlevel.hgame.input;

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
@ProtocolId(-1)
public class TriggerInputActionPacket implements Packet {
    private int actorId;
    private int actionId;
}
