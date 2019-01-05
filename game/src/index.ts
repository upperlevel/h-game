import * as Phaser from "phaser";
import Game = Phaser.Game;

import {ConnectingScene} from "./ui/connectingScene";
import {DisconnectedScene} from "./ui/disconnectedScene";
import {LoginScene} from "./ui/loginScene";
import {LobbyScene} from "./ui/lobby/lobbyScene";
import {GameScene} from "./ui/gameScene";

import {Keyboard} from "./actions";

class HGame extends Game {
    actions?: Keyboard;
    socket?: WebSocket;

    constructor() {
        super({
            title: "H-Game",
            url: "http://github.com/upperlevel/h-game",
            width: 720,
            height: 720,
            type: Phaser.AUTO,
            scene: [
                // The first scene is always started!
                ConnectingScene,

                DisconnectedScene,
                LoginScene,
                LobbyScene,
                GameScene,
            ],
            render: {
                pixelArt: true,
                antialias: false,
                autoResize: false
            }
        });
    }

    private customizeCanvas() {
        const canvas = this.canvas;
        canvas.style.position = "fixed";
        canvas.style.width = "100%";
        canvas.style.height = "100%";
        canvas.style.zIndex = "0";
    }

    private updateSize() {
        const canvas = this.canvas;
        this.resize(canvas.clientWidth, canvas.clientHeight);
    }

    load() {
        this.canvas = document.getElementsByTagName("canvas")[0];
        this.customizeCanvas();

        this.updateSize();
        window.addEventListener("resize", () => {
            this.updateSize();
        });
    }

    reconnect() {
        if (this.socket) {
            this.socket.onclose = null;
            this.socket!.close();
        }

        this.socket = new WebSocket("ws://localhost:8080/api/matchmaking");
        this.socket.onclose = () => {
            // TODO: find a way to stop all running scenes (may be one) and run DisconnectedScene.
            console.log("Disconnected :(");
        }
    }

    getChannel() {
        return this.socket!;
    }

    setJsonChannel(onMessage: (packet: any) => void) {
        this.socket!.onmessage = packet => onMessage(JSON.parse(packet.data));
    }

    dropJsonChannel() {
        this.socket!.onmessage = null;
    }
}

export const hgame = new HGame();
window.addEventListener("load", () => {
    hgame.load();
});
