import {SceneWrapper} from "../sceneWrapper"

import * as Phaser from "phaser";
import Container = Phaser.GameObjects.Container;
import Group = Phaser.GameObjects.Group;
import Text = Phaser.GameObjects.Text;

import {LobbyOverlay} from "./lobbyOverlay";

import {CurrentLobbyInfoPacket} from "@common/matchmaking/protocol";
import {LobbyPlayerInfo} from "@common/matchmaking/protocol";

export class LobbyScene extends SceneWrapper {
    overlay: LobbyOverlay;
    players: Container[] = [];

    constructor() {
        super("lobby");

        this.overlay = new LobbyOverlay(this);
    }

    setPlayers(packet: CurrentLobbyInfoPacket) {
        for (const player of this.players) {
            player.destroy(true);
        }
        this.players = [];

        for (const player of packet.players) {
            this.addPlayer(player);
        }

        (this.players[packet.admin].getAt(0) as Text).setColor("yellow").updateText();
    }

    addPlayer(player: LobbyPlayerInfo) {
        const container = this.add.container(0, 300);

        const sprite = this.add.sprite(0, 0, "santy").setDisplaySize(250, 250);
        const name = this.add.text(0, 0, player.name, {
            fontFamily: "pixeled",
            fontSize: 32,
            color: "white"
        });
        name.x -= name.displayWidth / 2.0;
        name.y -= (sprite.height * sprite.scaleY) / 2.0 + name.displayHeight / 2.0 + 30.0;

        // The name must be the first item of the container
        container.add(name);
        container.add(sprite);

        if (player.ready) {
            const ready = this.add.text(0, 0, "Ready", {
                fontFamily: "pixeled",
                fontSize: 16,
                color: "lime"
            });
            ready.x -= ready.displayWidth / 2.0;
            ready.y -= (sprite.height * sprite.scaleY) / 2.0 + ready.displayHeight / 2.0 + 15.0;

            container.add(ready);
        }

        container.active = true;

        this.players.push(container);
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
