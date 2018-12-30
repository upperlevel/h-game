package xyz.upperlevel.hgame.server

import xyz.upperlevel.hgame.matchmaking.CurrentLobbyInfoPacket
import xyz.upperlevel.hgame.matchmaking.LobbyPlayerInfo
import xyz.upperlevel.hgame.matchmaking.MatchBeginPacket

class Lobby(
        val registry: LobbyRegistry,
        val id: Long,
        admin: Player) {

    private var _state = LobbyState.PRE_GAME

    val state: LobbyState
        get() = _state

    private var _players = hashSetOf(admin)

    val players: Set<Player>
        get() = _players

    var admin: Player = admin

    var maxPlayers = 4

    init {
        admin.lobby = this
    }

    fun onQuit(player: Player) {
        _players.remove(player)

        if (_players.isEmpty()) {
            _state = LobbyState.DONE
            registry.onLobbyDelete(this)
            return
        }

        if (player == admin) {
            admin = _players.first()
        }

        broadcastLobbyInfo()
    }

    fun onJoin(player: Player): String? {
        when (state) {
            LobbyState.PRE_GAME -> {}
            LobbyState.PLAYING -> return "game already started"
            LobbyState.DONE -> return "lobby expired"
        }
        if (player in _players) return "player already in lobby"
        if (_players.size >= maxPlayers) return "max players reached"

        _players.add(player)
        player.lobby = this
        player.invalidateSentInvites()

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

    fun createInfoPacket(): CurrentLobbyInfoPacket {
        var adminIndex = -1
        val players = players.mapIndexed { index, player ->
            if (player == admin) adminIndex = index
            LobbyPlayerInfo(player.name!!, player.character, player.ready)
        }

        return CurrentLobbyInfoPacket(
                players,
                adminIndex
        )
    }

    fun broadcastLobbyInfo() {
        val packet = createInfoPacket()
        players.forEach {
            it.channel.writeAndFlush(packet)
        }
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



