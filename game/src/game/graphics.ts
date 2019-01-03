import * as Phaser from "phaser"

import {LobbyScene} from "./lobby"

export class Graphics extends Phaser.Game {
    constructor() {
        super({
            title: "H-Game",
            url: "http://github.com/upperlevel/h-game",
            parent: "game",
            width: 720,
            height: 720,
            type: Phaser.AUTO,
            render: {
                pixelArt: true,
                antialias: false,
                autoResize: false
            }
        });

        this.scene.add("lobby", LobbyScene);
        this.scene.start("lobby");

        this.customizeCanvas();

        this.updateSize();
        window.addEventListener("resize", () => this.updateSize());
    }

    customizeCanvas() {
        const canvas = this.canvas;
        canvas.style.position = "fixed";
        canvas.style.width = "100%";
        canvas.style.height = "100%";
        canvas.style.zIndex = "0";
    }

    updateSize() {
        const canvas = this.canvas;
        this.resize(canvas.clientWidth, canvas.clientHeight);
    }
}
