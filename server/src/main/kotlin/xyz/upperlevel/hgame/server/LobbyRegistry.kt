package xyz.upperlevel.hgame.server

class LobbyRegistry {
    private var nextId = 0L

    private val lobbiesById = HashMap<Long, Lobby>()


    fun getFromId(id: Long): Lobby? {
        return lobbiesById[id]
    }

    fun create(creator: Player): Lobby {
        creator.lobby?.onQuit(creator)

        val lobby = Lobby(
                this,
                nextId++,
                creator
        )

        lobbiesById[lobby.id] = lobby

        return lobby
    }

    fun onLobbyDelete(lobby: Lobby) {
        // Called when every player leaves the lobby,
        // simply delete the lobby
        synchronized(this) {
            lobbiesById.remove(lobby.id)
        }
    }
}
