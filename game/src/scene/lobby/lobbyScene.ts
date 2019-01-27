import {Scene} from "../scene";

import {LobbyOverlay} from "./lobbyOverlay";

import * as proto from "../../../../common/src/matchmaking/protocol";
import {GameConnector} from "../../connector/gameConnector";
import {HGame} from "../../index";
import {GameScene} from "../game/gameScene";
import {ConnectingScene} from "../connectingScene";
import {LobbyWorld} from "./lobbyWorld";

export class LobbyScene implements Scene {
    game: HGame;

    world: LobbyWorld;
    overlay: LobbyOverlay;

    chosenCharacter: string = "santy";

    constructor(game: HGame) {
        this.game = game;

        this.world = new LobbyWorld(game);
        this.overlay = new LobbyOverlay(this);
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
                this.world.onInfo(packet);
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
