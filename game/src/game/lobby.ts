import * as Phaser from "phaser"

export class LobbyScene extends Phaser.Scene {
    preload(): void {
        this.load.image("sky", "assets/game/sky.png");
        this.load.spritesheet("santy", "assets/game/santy.png", {frameWidth: 48, frameHeight: 48});
    }

    create(): void {
        this.anims.create({
            key: "idle",
            frames: this.anims.generateFrameNumbers("santy", {start: 0, end: 1}),
            frameRate: 6,
            repeat: -1
        });

        const santy = this.add.sprite(400, 300, "santy").setScale(4);
        santy.anims.load("idle");
        santy.anims.play("idle");
    }
}
