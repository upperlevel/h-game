import {MatchmakingPacket} from "../../common/src/matchmaking/protocol"
import * as ws from 'ws';
import {Lobby, LobbyRegistry} from "./lobby";


export class Player {
    playerRegistry: PlayerRegistry;

    private _name?: string;
    lobby?: Lobby;
    character?: string;
    ready: boolean = false;

    receivedInvites: Set<Player> = new Set();
    sentInvites: Set<Player> = new Set();

    readonly socket: ws;
    relaySocket?: ws;

    constructor(playerRegistry: PlayerRegistry, socket: ws) {
        this.playerRegistry = playerRegistry;
        this.socket = socket;
    }

    get name(): string {
        if (this._name == null) {
            throw new Error("login not done yet")
        }
        return this._name
    }

    get isLoginDone(): boolean {
        return this._name != null
    }

    onLogin(name: string): boolean {
        if (!this.playerRegistry.onLogin(this, name)) return false;

        if (this.isLoginDone) throw new Error("Login already succeded");

        this._name = name;
        return true;
    }

    sendInvite(to: Player) {
        this.sentInvites.add(to);
        to.onInviteReceived(this);
    }

    private onInviteReceived(from: Player) {
        this.receivedInvites.add(from);

        this.sendPacket({
            type: "invite",
            kind: "INVITE_RECEIVED",
            player: from.name
        })
    }

    sendPacket(packet: MatchmakingPacket) {
        this.socket.send(JSON.stringify(packet))
    }

    acceptInvite(from: Player, lobbyRegistry: LobbyRegistry): string | undefined {
        if (!this.receivedInvites.has(from)) {
            return "Invite expired"
        }
        let lobby = from.lobby;
        if (lobby == null) {
            lobby = lobbyRegistry.create(from)
        }
        return lobby.onJoin(this)
    }

    invalidateSentInvites() {
        this.sentInvites.forEach((invited) => {
            invited.receivedInvites.delete(this);
        });
        this.sentInvites.clear();
    }

    onDisconnect() {
        this.invalidateSentInvites();

        this.playerRegistry.onDisconnect(this);

        if (this.lobby != null) {
            this.lobby.onQuit(this)
        }
    }
}

export class PlayerRegistry {
    players: { [name: string]: Player } = {};

    onConnect(socket: ws): Player {
        return new Player(this, socket);
    }

    onLogin(player: Player, name: string): boolean {
        if (name in this.players) {
            return false;
        }
        this.players[name] = player;
        return true;
    }

    onDisconnect(player: Player) {
        if (player.name != null) return;

        delete this.players[player.name];
    }

    getByName(name: string): Player | undefined {
        return this.players[name]
    }
}


