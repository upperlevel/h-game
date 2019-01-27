import {Scene} from "../scene";

import {LobbyOverlay} from "./lobbyOverlay";

import {CurrentLobbyInfoPacket} from "../../../../common/src/matchmaking/protocol";
import {GameConnector} from "../../connector/gameConnector";

import * as proto from "../../../../common/src/matchmaking/protocol"

import {EntityTypes} from "../../entity/entityTypes";
import {HGame} from "../../index";
import {World} from "../../world/world";
import {GameScene} from "../game/gameScene";
import {Player} from "../../entity/player/player";
import {ConnectingScene} from "../connectingScene";
import {LobbyPlayerHud} from "./lobbyPlayerHud";

export class LobbyScene implements Scene {
    game: HGame;

    overlay: LobbyOverlay;

    world: World;

    players = new Map<string, [Player, LobbyPlayerHud]>();

    chosenCharacter: string = "santy";

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
                    height: 1,
                    texture: "assets/game/dirt.png"
                },
                {
                    x: 0,
                    y: 1,
                    width: 13,
                    height: 1,
                    texture: "assets/game/grass.png"
                }
            ],
            texts: [],
            emitters: [],
            decorations: [
                {
                    x: 0,
                    y: 2,
                    width: 3,
                    height: 3,
                    texture: "assets/game/tree.png"
                },
                {
                    x: 6,
                    y: 2,
                    width: 3,
                    height: 3,
                    texture: "assets/game/tree.png"
                }
            ]
        });
    }

    setPlayers(packet: CurrentLobbyInfoPacket) {
        // Despawns old players
        for (const [player, hud] of this.players.values()) {
            this.world.despawn(player)
        }
        this.players.clear();

        // Spawns new players
        const step = this.world.width / (packet.players.length + 1);
        let distance = step;

        for (const data of packet.players) {
            data.character = data.character || "santy";

            const player = new Player(this.world, Player.createBody(this.world), false, EntityTypes.get(data.character!)!, {gameHud: false});
            const hud = new LobbyPlayerHud(player);
            player.huds.push(hud);
            player.name = data.name;
            player.x = distance;
            player.y = 10;
            hud.me = data.name === this.game.playerName;
            hud.leader = data.name === packet.admin;
            this.world.spawn(player);
            this.players.set(player.name, [player, hud]);
            console.log(`Spawning player: ${player.name}`);

            if (hud.me) {
                this.chosenCharacter = data.character;
            }

            distance += step;
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

                const game = new GameScene(this.game, {
                    playerIndex: packet.playerIndex,
                    playerCount: packet.playerCount,
                    playerName: this.game.playerName!!,
                    character: this.chosenCharacter,
                });
                this.game.sceneManager.setScene(new ConnectingScene(this.game.sceneManager, this.game.gameConnector, game));

                break;
        }
    }


    enable() {
        this.game.matchmakingConnector.subscribe("message", this.onMessage, this);

        // The main player will be drawn only after an answer to this packet will be received.
        this.requestInfo();

        this.world.setup();

        this.overlay.enable();
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

        this.overlay.disable();

        this.game.matchmakingConnector.unsubscribe("message", this.onMessage, this);
    }
}
