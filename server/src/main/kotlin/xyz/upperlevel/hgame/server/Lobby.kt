package xyz.upperlevel.hgame.server

import xyz.upperlevel.hgame.matchmaking.CurrentLobbyInfoPacket
import xyz.upperlevel.hgame.matchmaking.MatchBeginPacket

class Lobby(
        val registry: LobbyRegistry,
        val id: Long,
        val name: String,
        val casual: Boolean,
        val private: Boolean,
        val password: String?,
        val maxPlayers: Int) {

    private var _state = LobbyState.PRE_GAME

    val state: LobbyState
        get() = _state

    private var _players = HashSet<Player>()

    val players: Set<Player>
        get() = _players

    fun onQuit(player: Player) {
        _players.remove(player)

        if (_players.isEmpty()) {
            _state = LobbyState.DONE
            registry.onLobbyDelete(this)
        }
    }

    fun onJoin(player: Player, password: String?): String? {
        if (this.password != null && password != this.password) return "wrong password"
        when (state) {
            LobbyState.PRE_GAME -> {}
            LobbyState.PLAYING -> return "game already started"
            LobbyState.DONE -> return "lobby expired"
        }
        if (player in _players) return "player already in lobby"
        if (_players.size >= maxPlayers) return "max players reached"

        _players.add(player)
        player.lobby = this
        return null
    }

    fun refreshReady() {
        if (state != LobbyState.PRE_GAME) return
        if (_players.all { it.ready }) {
            startGame()
        }
    }

    fun startGame() {
        _players.forEachIndexed { index, player ->
            player.channel.writeAndFlush(MatchBeginPacket(player.name!!, index))
        }
    }

    fun toInfoPacket(): CurrentLobbyInfoPacket {
        return CurrentLobbyInfoPacket(
                id,
                name,
                _players.map { it.name!! },
                maxPlayers
        )
    }


    enum class LobbyState {
        /**
         * The players are in the lobby, the game hasn't started yet
         */
        PRE_GAME,
        /**
         * The players are playing
         */
        PLAYING,
        /**
         * Everyone's out of the lobby, the lobby gets deleted
         */
        DONE,
    }
}



