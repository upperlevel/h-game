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

data class CreateLobbyPacket(
        val name: String,
        val private: Boolean,
        val password: String,
        val maxPlayers: Int
) : MatchMakingPacket()

data class JoinLobbyPacket(
        val name: String,
        val password: String?
) : MatchMakingPacket()

data class ReadyToPlayPacket(
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

data class CurrentLobbyInfoPacket(
        val id: Long,
        val name: String,
        val players: List<String>,
        val maxPlayers: Int
) : MatchMakingPacket()

data class OperationResultPacket(val error: String?) : MatchMakingPacket()

// -------- PROTOCOL NAMES --------

object MatchMakingPackets {
    val packetToName = hashMapOf(
            // client ->  server
            LoginPacket::class.java to "login",
            CreateLobbyPacket::class.java to "create_lobby",
            JoinLobbyPacket::class.java to "join_lobby",
            ReadyToPlayPacket::class.java to "ready",
            // server -> client
            MatchBeginPacket::class.java to "match_begin",
            CurrentLobbyInfoPacket::class.java to "current_lobby",
            OperationResultPacket::class.java to "result"
    )
    val nameToPacket = packetToName.entries.associate { (k, v) -> v to k }
}