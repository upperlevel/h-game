package xyz.upperlevel.hgame.networking.events;

import io.netty.channel.Channel;
import xyz.upperlevel.hgame.event.Event;

public class ConnectionCloseEvent implements Event {
    private Channel channel;
}
