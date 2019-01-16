import {Scene} from "../../scene";

import {LobbyOverlay} from "./lobbyOverlay";

import {CurrentLobbyInfoPacket} from "../../../../../common/src/matchmaking/protocol";
import {GameConnector} from "../../../connector/gameConnector";

import * as proto from "../../../../../common/src/matchmaking/protocol"
import {GameSceneConfig} from "../../game/gameSceneConfig";

import {EntityType} from "../../../entity/entity";

export interface LobbyPlayer {
    name: string,
    character: EntityType,
    ready: boolean,
    admin: boolean,
    me: boolean,
    spawned?: Container,
}

export class LobbyScene implements Scene {
    overlay: LobbyOverlay;
    players = new Map<string, LobbyPlayer>();
    changeCharacterKey?: Key;

    constructor() {
        this.overlay = new LobbyOverlay(this);
    }

    setPlayers(packet: CurrentLobbyInfoPacket) {
        for (const player of this.players.values()) {
            player.spawned!.destroy(true);
        }
        this.players.clear();

        for (const player of packet.players) {
            this.addPlayer({
                name: player.name,
                character: player.character ? EntityTypes.fromId(player.character!)! : EntityTypes.playableTypes[0],
                ready: player.ready,
                admin: player.name == packet.admin,
                me: player.name == this.game.playerName
            });
        }
    }

    addPlayer(player: LobbyPlayer) {
    }

    requestInfo() {
        this.game.matchmakingConnector.send({type: "lobby_info_request"});
    }

    invite(username: string) {
        this.game.matchmakingConnector.send({
            type: "invite",
            kind: "INVITE_PLAYER",
            player: username
        });
    }

    acceptInvite(player: string) {
        this.game.matchmakingConnector.send({
            type: "invite",
            kind: "ACCEPT_INVITE",
            player: player
        });
    }

    setPlayerInfo(character: string = "santy", ready: boolean = false) {
        this.game.matchmakingConnector.send({
            type: "lobby_update",
            character: character,
            ready: ready
        });
    }

    onMessage(packet: proto.MatchmakingPacket) {
        switch (packet.type) {
            case "lobby_info":
                this.setPlayers(packet);
                break;
            case "match_begin":
                this.game.gameConnector = new GameConnector(packet.token);

                let gameConfig: GameSceneConfig = {
                    playerIndex: packet.playerIndex,
                    playerCount: packet.playerCount,
                    playerName: this.game.playerName!,
                    player: this.players.get(this.game.playerName!)!
                };

                this.scene.start("connecting", {
                    connector: this.game.gameConnector,
                    nextScene: "game",
                    nextSceneParams: gameConfig,
                });
                break;
        }
    }


    enable() {
        EntityTypes.load(this);
        this.game.matchmakingConnector.subscribe("message", this.onMessage, this);

        // The main player will be drawn only after an answer to this packet will be received.
        this.requestInfo();

        this.changeCharacterKey = this.input.keyboard.addKey("SPACE");

        this.overlay.show();
    }

    update(): void {
        const padding = 100;
        const width = this.game.canvas.clientWidth - padding * 2;
        const step = Math.floor(width / (this.players.size + 1));

        let distance = padding + step;
        for (const player of this.players.values()) {
            player.spawned!.x = distance;
            distance += step;
        }

        if (JustDown(this.changeCharacterKey!)) {
            const me = this.players.get(this.game.playerName!);
            if (me) {
                const index = (EntityTypes.playableTypes.indexOf(me.character) + 1) % EntityTypes.playableTypes.length;
                this.setPlayerInfo(EntityTypes.playableTypes[index].id, me.ready);
            }
        }
    }

    disable() {
        this.overlay.hide();

        this.game.matchmakingConnector.unsubscribe("message", this.onMessage, this);
    }
}
