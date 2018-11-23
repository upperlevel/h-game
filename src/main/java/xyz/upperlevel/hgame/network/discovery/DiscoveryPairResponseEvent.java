package xyz.upperlevel.hgame.network.discovery;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import xyz.upperlevel.hgame.event.CancellableEvent;
import xyz.upperlevel.hgame.event.Event;

import java.net.InetAddress;

@AllArgsConstructor
public class DiscoveryPairResponseEvent implements Event {
    @Getter
    private final InetAddress ip;

    @Getter
    private final String name;

    @Getter
    private final boolean success;
}
