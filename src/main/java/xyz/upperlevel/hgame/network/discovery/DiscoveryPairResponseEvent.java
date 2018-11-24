package xyz.upperlevel.hgame.network.discovery;

import lombok.AllArgsConstructor;
import lombok.Getter;
import xyz.upperlevel.hgame.event.Event;

import java.net.InetAddress;

@AllArgsConstructor
public class DiscoveryPairResponseEvent implements Event {
    @Getter
    private final InetAddress ip;

    @Getter
    private final String nickname;

    @Getter
    private final boolean success;
}
