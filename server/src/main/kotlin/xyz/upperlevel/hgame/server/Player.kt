package xyz.upperlevel.hgame.server

import io.netty.channel.Channel
import xyz.upperlevel.hgame.matchmaking.InvitePacket
import xyz.upperlevel.hgame.matchmaking.InvitePacketType
import java.lang.IllegalStateException
import java.lang.RuntimeException

class Player(val channel: Channel) {
    var name: String? = null

    val isLoginDone: Boolean
        get() = name != null

    var lobby: Lobby? = null
    var character: String? = null
    var ready = false

    var relayChannel: Channel? = null


    // The invite system is quite strange
    // when you invite someone the invite is valid until you
    // join another invite, when you do the invites you sent get invalidated.
    // To simulate this behaviour we create two sets with the invites that
    // we sent and the ones we received, when a player joins a lobby
    // that he didn't create his sentInvites gets cleared
    // and the receivedInvites of the players that were in there
    // are modified accordingly.
    val receivedInvites = HashSet<Player>()
    val sentInvites = HashSet<Player>()


    fun onLogin(name: String) {
        if (isLoginDone) throw IllegalStateException("Login already succeed")
        this.name = name
    }

    fun sendInvite(to: Player) {
        sentInvites.add(to)
        to.onInviteReceived(this)
    }

    fun onInviteReceived(inviter: Player) {
        receivedInvites.add(inviter)
        channel.writeAndFlush(InvitePacket(InvitePacketType.INVITE_RECEIVED, inviter.name!!))
    }

    fun acceptInvite(from: Player, lobbyRegistry: LobbyRegistry): String? {
        if (from !in receivedInvites) {
            return "Invite expired"
        }

        val lobby = from.lobby ?: lobbyRegistry.create(from)

        return lobby.onJoin(this)
    }

    fun invalidateSentInvites() {
        sentInvites.forEach {
            it.receivedInvites.remove(this)
        }
        sentInvites.clear()
    }
}