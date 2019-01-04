import {Player} from "./player";
import {CurrentLobbyInfoPacket, LobbyPlayerInfo} from "@common/matchmaking/protocol"

type LobbyState =
    /**
     * The players are in the lobby, the game hasn't started yet
     */
    "PRE_GAME"
    /**
     * Waiting for the players to initialize the other websocket connection
     */
    | "CONNECTION_WAIT"
    /**
     * The players are playing
     */
    | "PLAYING"
    /**
     * Everyone's out of the lobby, the lobby gets deleted
     */
    | "DONE";

export class Lobby {
    admin: Player;
    private _state: LobbyState = "PRE_GAME";

    maxPlayers = 4;

    get state(): LobbyState {
        return this._state
    }

    players = new Set<Player>();

    constructor(admin: Player) {
        this.admin = admin;
        this.onJoin(admin, false)
    }

    onQuit(player: Player) {
        this.players.delete(player);

        if (this.players.size == 0) {
            this._state = "DONE";
            return;
        }

        if (player == this.admin) {
            this.admin = this.players.keys().next().value
        }

        this.broadcastLobbyInfo()
    }

    onJoin(player: Player, invalidateInvites: boolean = true): string | undefined {
        switch (this.state) {
            case "PRE_GAME": break;
            case "PLAYING": return "game already started";
            case "DONE": return "max players reached";
        }
        if (this.players.has(player)) return "player already in lobby";
        if (this.players.size >= this.maxPlayers) return "max players reached";

        this.players.add(player);
        player.lobby = this;
        player.ready = false;
        player.invalidateSentInvites();

        return undefined;
    }

    refreshReady() {
        if (this.state != "PRE_GAME" && this.state != "CONNECTION_WAIT") return;
        for (let player of this.players.values()) {
            if (!player.ready) return;
        }
        for (let player of this.players.values()) {
            player.ready = false
        }
        // Everyone's ready, start the game
        switch (this.state) {
            case "PRE_GAME": this.startGame(); break;
            case "CONNECTION_WAIT":  this.notifyConnectionReady(); break;
        }
    }

    startGame() {
        let i = 0;

        this._state = "PLAYING";

        this.players.forEach((player) => {
            player.sendPacket({
                type: "match_begin",
                token: player.name,
                playerIndex: i
            });
            i++
        })
    }

    notifyConnectionReady() {
        this.players.forEach(player => {
            player.relaySocket!.send("ready")
        });
        status = "PLAYING";
    }

    // TODO: onJoin, refreshReady, startGame

    createInfoPacket(): CurrentLobbyInfoPacket {
        let adminIndex = -1;
        let i = 0;
        let players = new Array<LobbyPlayerInfo>();

        this.players.forEach((player) => {
            if (player == this.admin) adminIndex = i;
            players.push({
                "name": player.name,
                "character": player.character,
                "ready": player.ready,
            });
            i++;
        });

        return {
            "type": "lobby_info",
            "players": players,
            "admin": adminIndex,
        }
    }

    broadcastLobbyInfo() {
        const packet = this.createInfoPacket();
        this.players.forEach((player) => {
            player.sendPacket(packet)
        });
    }
}

export class LobbyRegistry {
    create(creator: Player): Lobby {
        if (creator.lobby != null) {
            creator.lobby.onQuit(creator)
        }

        return new Lobby(creator);
    }
}
