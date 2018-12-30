package xyz.upperlevel.hgame.matchmaking


/**
 * Quick explanation of the protocol,
 * On the initialization the client sends:"version: {version}\n{purpose}"
 * where version is the version of the protocol and purpose is the connection purpose
 * if this works alright an "ok" packet is sent by the server the connection protocol is switched to the new one.
 * The purposes are: "matchmaking" and "play"
 *
 * the "matchmaking" protocol sends in the first line the packet type and in the second line the packet json-serialized
 * every client request is followed by an OperationResultPacket stating the error (or null if there wasn't any).
 * The first packet should be a LoginPacket (and it receives the OperationResultPacket too)
 *
 * the "play" purpose also takes a token after the purpose (separated by a space)
 * that token is given by the MatchBeginPacket and is implementation-specific.
 * After that every websocket frame is forwarded to the other players in the game until the end of the connection.
 */

open class MatchMakingPacket

// TODO: casual lobby join
//class JoinCasualLobbyPacket : MatchMakingPacket()

data class LoginPacket(val name: String) : MatchMakingPacket()

data class PlayerLobbyInfoChangePacket(
        val character: String,
        val ready: Boolean
) : MatchMakingPacket()

/* TODO: lobby discovery
data class LobbyDiscoverPacket : MatchMakingPacket()

data class LobbyDiscoverInfo(
    val name: String,
    val private: Boolean,
    val playerCount: Int
)

data class LobbyDiscoverResponsePacket(val lobbies: List<LobbyDiscoverInfo>) : MatchMakingPacket()
*/

// -------- RESPONSES --------

// The token is needed by the new connection to start a relay

data class MatchBeginPacket(
        val token: String,
        val playerIndex: Int
) : MatchMakingPacket()

data class LobbyPlayerInfo(
        val name: String,
        val character: String?,
        val ready: Boolean
)

/**
 * Sends all the info about the current lobby
 * The admin is encoded as the index of the previous list
 */
data class CurrentLobbyInfoPacket(
        val players: List<LobbyPlayerInfo>,
        var admin: Int
) : MatchMakingPacket()

data class OperationResultPacket(val error: String?) : MatchMakingPacket()


// -------- REQUEST/RESPONSES --------

enum class InvitePacketType {
    /**
     * Client -> Server
     * Invite player with that name (a response will be sent after this).
     */
    INVITE_PLAYER,
    /**
     * Server -> Client
     * You have received an invite from "player".
     */
    INVITE_RECEIVED,
    /**
     * Client -> Server
     * I (client) accept the invite from "player".
     * Note that there's no packet for invite rejection
     */
    ACCEPT_INVITE,
}

data class InvitePacket(
        val type: InvitePacketType,
        val player: String
) : MatchMakingPacket()

// -------- PROTOCOL NAMES --------

object MatchMakingPackets {
    val packetToName = hashMapOf(
            // client ->  server
            LoginPacket::class.java to "login",
            PlayerLobbyInfoChangePacket::class.java to "lobby_update",
            // server -> client
            MatchBeginPacket::class.java to "match_begin",
            CurrentLobbyInfoPacket::class.java to "lobby_info",
            OperationResultPacket::class.java to "result",
            // client <-> server
            InvitePacket::class.java to "invite"
    )
    val nameToPacket = packetToName.entries.associate { (k, v) -> v to k }
}