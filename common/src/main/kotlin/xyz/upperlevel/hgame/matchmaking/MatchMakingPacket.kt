package xyz.upperlevel.hgame.matchmaking

open class MatchMakingPacket

// TODO: casual room join
//class JoinCasualRoomPacket : MatchMakingPacket()

data class CreateRoomPacket(
        val name: String,
        val private: Boolean,
        val password: String,
        val maxPlayers: Int
) : MatchMakingPacket()

data class JoinRoomPacket(
        val name: String,
        val password: String?
) : MatchMakingPacket()

/* TODO: room discovery
data class RoomDiscoverPacket : MatchMakingPacket()

data class RoomDiscoverInfo(
    val name: String,
    val private: Boolean,
    val playerCount: Int
)

data class RoomDiscoverResponsePacket(val rooms: List<RoomDiscoverInfo>) : MatchMakingPacket()
*/