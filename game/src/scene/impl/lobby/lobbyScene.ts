import {Scene} from "../../scene";
import {SceneManager} from "../../sceneManager";

import {LobbyOverlay} from "./lobbyOverlay";

import {CurrentLobbyInfoPacket} from "../../../../../common/src/matchmaking/protocol";
import {GameConnector} from "../../../connector/gameConnector";

import * as proto from "../../../../../common/src/matchmaking/protocol"
import {GameSceneConfig} from "../../game/gameSceneConfig";

import {EntityTypes} from "../../../entity/entityTypes";
import {EntityType} from "../../../entity/entityType";
import {HGame} from "../../../index";
import {Key} from "../../../util/key";
import {World} from "../../../world";

export interface LobbyPlayer {
    name: string,
    character: EntityType,
    ready: boolean,
    admin: boolean,
    me: boolean
}

export class LobbyScene implements Scene {
    game: HGame;

    overlay: LobbyOverlay;

    world: World;

    players = new Map<string, LobbyPlayer>();
    changeCharacterKey: Key;

    constructor(game: HGame) {
        this.game = game;


        this.overlay = new LobbyOverlay(this);

        this.world = new World();
        this.changeCharacterKey = new Key("Space");
    }

    setPlayers(packet: CurrentLobbyInfoPacket) {
        for (const player of packet.players) {
            console.log(`Player: name=${player.name} character=${player.character}`);
            if (!player.character) {
                player.character = "santy";
            }

            const entity = EntityTypes.get(player.character!)!.create(this.world, false);
            this.game.app.stage.addChild(entity.sprite);

            this.world.spawn(entity);
        }
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
        this.game.matchmakingConnector.subscribe("message", this.onMessage, this);

        // The main player will be drawn only after an answer to this packet will be received.
        this.requestInfo();

        this.changeCharacterKey.subscribe();

        // this.overlay.show();
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
        // this.overlay.hide();
        this.changeCharacterKey.unsubscribe();

        this.game.matchmakingConnector.unsubscribe("message", this.onMessage, this);
    }
}
