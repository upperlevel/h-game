package xyz.upperlevel.hgame.network.events

import io.netty.channel.Channel
import xyz.upperlevel.hgame.event.Event

class ConnectionCloseEvent(val channel: Channel) : Event
