package xyz.upperlevel.hgame.network.discovery;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import xyz.upperlevel.hgame.event.CancellableEvent;
import xyz.upperlevel.hgame.event.Event;

import java.net.InetAddress;

@Data
@AllArgsConstructor
public class DiscoveryPairResponseEvent implements Event {
    private InetAddress ip;
    private boolean success;
}
