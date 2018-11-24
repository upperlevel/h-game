package xyz.upperlevel.hgame.network.discovery;

import lombok.AllArgsConstructor;
import lombok.Getter;
import xyz.upperlevel.hgame.event.CancellableEvent;

import java.net.InetAddress;

@AllArgsConstructor
public class DiscoveryPairRequestEvent extends CancellableEvent {
    @Getter
    private final InetAddress ip;

    @Getter
    private final String nickname;
}
