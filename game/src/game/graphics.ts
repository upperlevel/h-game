import * as Phaser from "phaser"

import {LobbyScene} from "./lobby_scene"
import {GameScene} from "./game_scene";

export class Graphics extends Phaser.Game {
    constructor() {
        super({
            title: "H-Game",
            url: "http://github.com/upperlevel/h-game",
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
        this.scene.add("game", GameScene);
        this.scene.start("lobby");

        this.canvas = document.getElementsByTagName("canvas")[0];
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
        console.log(`Resizing game: w=${canvas.clientWidth} h=${canvas.clientHeight}`)
    }
}
