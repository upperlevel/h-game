package xyz.upperlevel.hgame.matchmaking

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

data class MatchBeginPacket(val id: Long) : MatchMakingPacket()

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