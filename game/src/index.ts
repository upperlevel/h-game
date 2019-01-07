import * as Phaser from "phaser";
import Game = Phaser.Game;

import {ConnectingScene} from "./scenes/connectingScene";
import {DisconnectedScene} from "./scenes/disconnectedScene";
import {LoginScene} from "./scenes/loginScene";
import {LobbyScene} from "./scenes/lobby/lobbyScene";
import {GameScene} from "./scenes/gameScene";

import {Keyboard} from "./actions";

export class HGame extends Game {
    socket?: WebSocket;

    playerName?: string;

    constructor() {
        super({
            title: "H-Game",
            url: "http://github.com/upperlevel/h-game",
            width: 720,
            height: 720,
            type: Phaser.AUTO,
            physics: {
                default: 'arcade',
                arcade: {
                    gravity: { y: 600 },
                    debug: false
                }
            },
            scene: [
                // The first scene is always started -_-
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

        this.canvas = document.getElementsByTagName("canvas")[0];
        this.customizeCanvas();

        this.updateSize();
        window.addEventListener("resize", () => {
            this.updateSize();
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

    reconnect() {
        if (this.socket) {
            this.socket.onclose = null;
            this.socket!.close();
        }

        this.socket = new WebSocket("ws://localhost:8080/api/matchmaking");

        this.socket.onmessage = event => {
            const raw = event.data;
            console.log(`Web-socket received: ${raw}`);

            this.events.emit("message", JSON.parse(raw));
            console.log("Done: ", raw);
        };

        this.socket.onclose = () => {
            // TODO: find a way to stop all running scenes (may be one) and run DisconnectedScene.
            console.log("Disconnected :(");
        }
    }

    send(packet: any) {
        const raw = JSON.stringify(packet);
        console.log(`Web-socket sent: ${raw}`);

        if (this.socket != null) {
            this.socket.send(raw);
        }
    }

    getChannel() {
        return this.socket!;
    }
}

window.addEventListener("load", () => new HGame());
