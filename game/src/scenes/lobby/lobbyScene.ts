import {SceneWrapper} from "../sceneWrapper"

import * as Phaser from "phaser";
import Container = Phaser.GameObjects.Container;
import {TextContainer} from "../textContainer";

import {LobbyOverlay} from "./lobbyOverlay";

import {CurrentLobbyInfoPacket} from "@common/matchmaking/protocol";
import {GameConnector} from "../../connector/gameConnector";

import * as proto from "@common/matchmaking/protocol"
import {GameScene} from "../game/gameScene";
import {GameSceneConfig} from "../game/gameSceneConfig";

interface LobbyPlayer {
    name: string,
    character?: string,
    ready: boolean,
    admin: boolean,
    me: boolean,
}

export class LobbyScene extends SceneWrapper {
    overlay: LobbyOverlay;
    players: Container[] = [];

    constructor() {
        super({key: "lobby"});

        this.overlay = new LobbyOverlay(this);
    }

    setPlayers(packet: CurrentLobbyInfoPacket) {
        for (const player of this.players) {
            player.destroy(true);
        }
        this.players = [];

        for (const player of packet.players) {
            this.addPlayer({
                name: player.name,
                character: player.character,
                ready: player.ready,
                admin: player.name == packet.admin,
                me: player.name == this.game.playerName
            });
        }
    }

    addPlayer(player: LobbyPlayer) {
        const container = this.add.container(0, 300);

        const sprite = this.add.sprite(0, 0, "santy").setDisplaySize(250, 250);
        container.setName("sprite");
        container.add(sprite);

        const aboveHead = new TextContainer(this, 0, 0, 5.0);
        aboveHead.setName("aboveHead");

        aboveHead.addLine(player.name, true, {
            fontFamily: "pixeled",
            fontSize: 32,
            color: player.me ? "blue" : (player.admin ? "yellow" : "white")
        });

        if (player.admin) {
            aboveHead.addLine("(Leader)", true, {
                fontFamily: "pixeled",
                fontSize: 24,
                color: "yellow"
            });
        }

        if (player.ready) {
            aboveHead.addLine("Ready", true, {
                fontFamily: "pixeled",
                fontSize: 16,
                color: "lime"
            });
        }

        const floating = 64.0;
        aboveHead.y -= sprite.displayHeight / 2.0 + aboveHead.displayHeight / 2.0 + floating;

        container.add(aboveHead);

        this.players.push(container);

        return container;
    }

    /**
     * Sends a request to the server in order to have the full list of the current lobby's players.
     */
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
            character: "santy",
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
                };

                this.scene.start("connecting", {
                    connector: this.game.gameConnector,
                    nextScene: "game",
                    nextSceneParams: gameConfig,
                });
                break;
        }
    }

    onPreload() {
        this.load.spritesheet("santy", "assets/game/santy.png", {frameWidth: 48, frameHeight: 48});
    }

    onCreate() {
        this.game.matchmakingConnector.events.on("message", this.onMessage, this);

        // The main player will be drawn only after an answer to this packet will be received.
        this.requestInfo();

        this.overlay.show();
    }

    onUpdate(): void {
        const padding = 100;
        const width = this.game.canvas.clientWidth - padding * 2;
        const step = Math.floor(width / (this.players.length + 1));

        let distance = padding + step;
        for (const player of this.players) {
            player.x = distance;
            distance += step;
        }
    }

    onShutdown() {
        this.overlay.hide();

        this.game.matchmakingConnector.events.removeListener("message", this.onMessage, this, false);
    }
}
