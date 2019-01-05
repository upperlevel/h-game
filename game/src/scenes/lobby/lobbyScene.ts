import {SceneWrapper} from "../sceneWrapper"

import * as Phaser from "phaser";
import Container = Phaser.GameObjects.Container;

import {LobbyOverlay} from "./lobbyOverlay";
import {hgame} from "../../index";


export class LobbyScene extends SceneWrapper {
    overlay: LobbyOverlay;
    players: Container[] = [];

    constructor() {
        super("lobby");

        this.overlay = new LobbyOverlay();
    }

    addPlayer(playerName: string) {
        const player = this.add.container(400, 300);

        const sprite = this.add.sprite(0, 0, "santy").setDisplaySize(250, 250);

        // TODO: play idle animation.
        //sprite.anims.load("idle");
        //sprite.anims.play("idle");

        const name = this.add.text(0, 0, playerName, {
            fontFamily: "pixeled",
            fontSize: 32,
            color: "yellow"
        });
        name.x -= name.displayWidth / 2.0;
        name.y -= (sprite.height * sprite.scaleY) / 2.0 + name.displayHeight / 2.0 + 15;

        player.add(sprite);
        player.add(name);

        player.active = true;

        this.players.push(player);
    }

    onPreload() {
        this.load.spritesheet("santy", "assets/game/santy.png", {frameWidth: 48, frameHeight: 48});
    }

    onCreate() {
        this.addPlayer("You");

        this.overlay.show();
    }

    onUpdate(): void {
        const padding = 100;
        const width = hgame.canvas.clientWidth - padding * 2;
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
