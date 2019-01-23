import {Scene} from "../../scene";

import {LobbyOverlay} from "./lobbyOverlay";

import {CurrentLobbyInfoPacket} from "@common/matchmaking/protocol";
import {GameConnector} from "../../../connector/gameConnector";

import * as proto from "../../../../../common/src/matchmaking/protocol"

import {EntityTypes} from "../../../entity/entityTypes";
import {HGame} from "../../../index";
import {World} from "../../../world/world";
import {LobbyPlayer} from "./lobbyPlayer";
import {GameSceneConfig} from "../../game/gameScene";

export class LobbyScene implements Scene {
    game: HGame;

    overlay: LobbyOverlay;

    world: World;

    constructor(game: HGame) {
        this.game = game;

        this.overlay = new LobbyOverlay(this);

        this.world = new World(game.app, {
            id: "lobby",
            width: 13,
            height: 7,
            spawnPoints: [],
            platforms: [
                {
                    x: 0,
                    y: 0,
                    width: 13,
                    height: 2,
                    texture: "assets/game/debug.png"
                }
            ],
            texts: [
                {
                    x: 1,
                    y: 1,
                    height: 1,
                    text: "Hello world",
                    centered: false,
                    style: {
                        fontFamily: "pixeled",
                        fill: 0xff0000
                    }
                }
            ]
        });
    }

    setPlayers(packet: CurrentLobbyInfoPacket) {
        for (const player of packet.players) {
            console.log(`Player: name=${player.name} character=${player.character}`);
            if (!player.character) {
                player.character = "santy";
            }

            const entity = new LobbyPlayer(this.world, false, EntityTypes.get(player.character!)!);
            entity.x = 2;
            entity.y = 10;
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

        this.world.setup();

        // this.overlay.show();
    }

    update(delta: number): void {
        this.world.update(delta);

        // TODO change player's skin on keyboard click
    }

    resize() {
        this.world.resize();
    }

    disable() {
        this.world.destroy();

        // this.overlay.hide();

        this.game.matchmakingConnector.unsubscribe("message", this.onMessage, this);
    }
}
