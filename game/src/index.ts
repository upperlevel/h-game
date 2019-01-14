import * as PIXI from "pixi.js"

import {SceneManager} from "./scene/sceneManager";

import {MatchmakingConnector} from "./connector/matchmakingConnector";
import {GameConnector} from "./connector/gameConnector";

export class HGame {
    app: PIXI.Application;
    sceneManager: SceneManager;

    matchmakingConnector: MatchmakingConnector;
    gameConnector?: GameConnector;

    playerName?: string;

    constructor() {
        this.app = new PIXI.Application();
        document.body.appendChild(this.app.view);

        window.addEventListener("resize", () => {
            this.app.renderer.resize(window.innerWidth, window.innerHeight);
        });

        this.sceneManager = new SceneManager();
        // TODO this.sceneManager.setScene(ConnectingScene);

        this.matchmakingConnector = new MatchmakingConnector();
    }
}

window.addEventListener("load", () => new HGame());
