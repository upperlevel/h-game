import * as Phaser from "phaser";
import Game = Phaser.Game;
import Scene = Phaser.Scene;

import {ConnectingScene} from "./scenes/connection/connectingScene";
import {DisconnectedScene} from "./scenes/connection/disconnectedScene";
import {LoginScene} from "./scenes/login/loginScene";
import {LobbyScene} from "./scenes/lobby/lobbyScene";
import {GameScene} from "./scenes/game/gameScene";

import {Keyboard} from "./actions";
import {MatchmakingConnector} from "./connector/matchmakingConnector";
import {GameConnector} from "./connector/gameConnector";

export class HGame extends Game {
    matchmakingConnector: MatchmakingConnector;

    // Will be initialized only after the matchmaking phase.
    gameConnector?: GameConnector;

    playerName?: string;

    constructor() {
        super({
            title: "H-Game",
            url: "http://github.com/upperlevel/h-game",
            width: 720,
            height: 720,
            type: Phaser.AUTO,
            physics: {
                default: "arcade",
                arcade: {
                    gravity: {y: 50.0},
                    debug: false
                }
            },
            scene: [
                // The first scene must always have {active: false}
                // to avoid it from automatically be started.
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

        this.matchmakingConnector = new MatchmakingConnector();
        //this.gameConnector = new GameConnector(this);

        this.scene.start("connecting", {connector: this.matchmakingConnector, nextScene: "login"});

        window.addEventListener('resize', () => {
            this.resize(window.innerWidth, window.innerHeight);
            this.scene.scenes.forEach((s) => {
                s.cameras.resize(window.innerWidth, window.innerHeight);
            })
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
}

window.addEventListener("load", () => new HGame());
