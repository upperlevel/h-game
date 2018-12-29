package xyz.upperlevel.hgame.server

class LobbyRegistry {
    private var nextId = 0L

    private val lobbiesById = HashMap<Long, Lobby>()
    private val lobbiesByName = HashMap<String, Lobby>()


    fun getFromName(name: String): Lobby? {
        return lobbiesByName["name"]
    }

    fun getFromId(id: Long): Lobby? {
        return lobbiesById[id]
    }

    fun create(
            creator: Player,
            name: String,
            private: Boolean,
            password: String?,
            maxPlayers: Int): Lobby {
        creator.lobby?.onQuit(creator)

        val lobby = Lobby(
                this,
                nextId++,
                name,
                false,
                private,
                password,
                maxPlayers
        )

        if (lobbiesByName.putIfAbsent(name, lobby) != null) {
            throw IllegalArgumentException("name already in use!")
        }
        lobbiesById[lobby.id] = lobby

        lobby.onJoin(creator, password)

        return lobby
    }

    fun onLobbyDelete(lobby: Lobby) {
        // Called when every player leaves the lobby,
        // simply delete the lobby
        synchronized(this) {
            lobbiesById.remove(lobby.id)
        lobbiesByName.remove(lobby.name)
        }
    }
}
