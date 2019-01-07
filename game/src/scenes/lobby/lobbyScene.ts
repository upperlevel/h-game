import {SceneWrapper} from "../sceneWrapper"

import * as Phaser from "phaser";
import Text = Phaser.GameObjects.Text;

import {LobbyOverlay} from "./lobbyOverlay";

import {CurrentLobbyInfoPacket} from "@common/matchmaking/protocol";
import Container = Phaser.GameObjects.Container;
import {TextContainer} from "../textContainer";

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
        super("lobby");

        this.overlay = new LobbyOverlay(this);
    }

    setPlayers(packet: CurrentLobbyInfoPacket) {
        console.log("Set players: ", packet);
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

    onPreload() {
        this.load.spritesheet("santy", "assets/game/santy.png", {frameWidth: 48, frameHeight: 48});
    }

    onCreate() {
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
    }
}
