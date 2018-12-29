package xyz.upperlevel.hgame.server

class RoomRegistry {
    private var nextId = 0L

    private val roomsById = HashMap<Long, Room>()
    private val roomsByName = HashMap<String, Room>()


    fun getFromName(name: String): Room? {
        return roomsByName["name"]
    }

    fun getFromId(id: Long): Room? {
        return roomsById[id]
    }

    fun create(
            name: String,
            private: Boolean,
            password: String?,
            maxPlayers: Int): Room {

        val room = Room(
                nextId++,
                name,
                false,
                private,
                password,
                maxPlayers
        )

        if (roomsByName.putIfAbsent(name, room) != null) {
            throw IllegalArgumentException("name already in use!")
        }
        roomsById[room.id] = room
        return room
    }
}
