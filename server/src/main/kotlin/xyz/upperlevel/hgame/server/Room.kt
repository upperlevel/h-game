package xyz.upperlevel.hgame.server

class Room(
        val id: Long,
        val name: String,
        val casual: Boolean,
        val private: Boolean,
        val password: String?,
        val maxPlayers: Int) {

    private var _state = RoomState.LOBBY

    val state: RoomState
        get() = _state

    private var players = HashSet<Player>()

    fun onQuit(player: Player) {
        players.remove(player)

        if (players.isEmpty()) {
            _state = RoomState.DONE
        }
    }

    fun onJoin(player: Player, password: String?): String? {
        if (this.password != null && password != this.password) return "wrong password"
        when (state) {
            RoomState.LOBBY -> {}
            RoomState.PLAYING -> return "game already started"
            RoomState.DONE -> return "room expired"
        }
        if (player in players) return "player already in room"
        if (players.size >= maxPlayers) return "max players reached"

        players.add(player)
        return null
    }



    enum class RoomState {
        /**
         * The players are in the lobby, the game hasn't started yet
         */
        LOBBY,
        /**
         * The players are playing
         */
        PLAYING,
        /**
         * Everyone's out of the room, the room gets deleted
         */
        DONE,
    }
}



