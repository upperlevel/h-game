import * as Phaser from "phaser"
import {hgame} from "../index";
import {Keyboard} from "../actions";

export class GameScene extends Phaser.Scene {
    preload(): void {
        this.load.spritesheet("santy", "assets/game/santy.png", {frameWidth: 48, frameHeight: 48});
    }

    create(): void {
        hgame.actions = new Keyboard(this);

        this.anims.create({
            key: "santy_idle",
            frames: this.anims.generateFrameNumbers("santy", {start: 0, end: 1}),
            frameRate: 6,
            repeat: -1
        });
        this.anims.create({
            key: "santy_left",
            frames: this.anims.generateFrameNumbers("santy", {start: 0, end: 2})
        });

        const santy = this.add.sprite(400, 300, "santy").setScale(4);
        santy.anims.load("idle");
        santy.anims.play("idle");
    }
}
