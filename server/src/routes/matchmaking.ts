import * as ws from 'ws';
import * as proto from "../../../common/src/matchmaking/protocol";
import {Player, PlayerRegistry} from '../player';
import {LobbyRegistry} from "../lobby";

class ConnectionHandler {
    lobbyRegistry: LobbyRegistry;

    socket: ws;
    player: Player;

    constructor(playerRegistry: PlayerRegistry, lobbyRegistry: LobbyRegistry, socket: ws) {
        this.lobbyRegistry = lobbyRegistry;
        this.socket = socket;
        this.player = playerRegistry.onConnect(socket);

        this.socket.on("message", this.onMessage.bind(this));
        this.socket.on("close", this.onDisconnect.bind(this));
        console.log("Socket initialized")
    }

    sendPacket(packet: proto.MatchmakingPacket) {
        this.socket.send(JSON.stringify(packet))
    }

    sendResult(error?: string) {
        this.sendPacket({
            type: "result",
            error: error
        })
    }

    onMessage(msg: ws.Data) {
        const packet = JSON.parse(msg as string) as proto.MatchmakingPacket;

        console.debug("Message: ", packet);

        if (!this.player.isLoginDone) {
            // Login needed
            if (packet.type != "login") {
                // Wrong packet
                this.sendResult("Login needed");
            } else if (!this.player.onLogin(packet.name)) {
                // Login error: name taken
                this.sendResult("Name already taken")
            } else {
                // Login succeeded
                this.sendResult()
            }
            return
        }

        switch (packet.type) {
            case "lobby_update": {
                this.onPlayerInfoChangePacket(packet);
                break;
            }
            case "invite": {
                this.onInvitePacket(packet);
                break;
            }
            default: {
                this.sendResult("Invalid packet type");
                break;
            }
        }
    }

    onPlayerInfoChangePacket(packet: proto.PlayerLobbyInfoChangePacket) {
        this.player.character = packet.character;
        // TODO: if player ready but no lobby joined then join a casual match

        this.sendResult(); // Ok

        if (this.player.lobby != null && this.player.ready != packet.ready) {
            this.player.ready = packet.ready;

            this.player.lobby.refreshReady();
        }

        if (this.player.lobby != null && this.player.lobby.state == "PRE_GAME") {
            // If the lobby hasn't started the game yet refresh the lobby info
            this.player.lobby.broadcastLobbyInfo();
        }
    }

    onInvitePacket(packet: proto.InvitePacket) {
        const packetPlayer = this.player.playerRegistry.getByName(packet.player);

        if (packetPlayer == null) {
            this.sendResult("Player not found");
            return;
        }

        switch (packet.kind) {
            case "INVITE_PLAYER": {
                this.player.sendInvite(packetPlayer);
                // This can't fail for now
                this.sendResult(); // No problem
                break;
            }
            case "ACCEPT_INVITE": {
                const error = this.player.acceptInvite(packetPlayer, this.lobbyRegistry);
                this.sendResult(error);
                if (error == null && this.player.lobby != null) {
                    // Notify the other players of the new friend in the lobby
                    this.player.lobby.broadcastLobbyInfo()
                }
                break;
            }
            default: {
                this.sendResult("Invalid invite type");
                break;
            }
        }

    }


    onDisconnect() {
        console.log("Player disconnected: ", this.player.name);
        this.player.onDisconnect()
    }
}


export class MatchMaker {
    playerRegistry: PlayerRegistry;
    lobbyRegistry: LobbyRegistry;

    constructor(playerRegistry: PlayerRegistry, lobbyRegistry: LobbyRegistry) {
        this.playerRegistry = playerRegistry;
        this.lobbyRegistry = lobbyRegistry;
    }

    connectionHandler(ws: ws): void {
        new ConnectionHandler(this.playerRegistry, this.lobbyRegistry, ws);
        console.log("Client connected")
    }
}
