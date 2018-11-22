package xyz.upperlevel.hgame.network.discovery;

import lombok.AllArgsConstructor;
import lombok.Data;
import xyz.upperlevel.hgame.event.Event;

import java.net.InetAddress;

@Data
@AllArgsConstructor
public class DiscoveryResponseEvent implements Event {
    private InetAddress ip;
    private String nickname;
}
