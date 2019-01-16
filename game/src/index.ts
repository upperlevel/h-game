import * as PIXI from "pixi.js"

import {SceneManager} from "./scene/sceneManager";

import {MatchmakingConnector} from "./connector/matchmakingConnector";
import {GameConnector} from "./connector/gameConnector";
import {ConnectingScene} from "./scene/impl/connectingScene";
import {LoginScene} from "./scene/impl/loginScene";

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

        this.matchmakingConnector = new MatchmakingConnector();

        this.sceneManager = new SceneManager();
        this.sceneManager.setScene(new ConnectingScene(this.sceneManager, this.matchmakingConnector, new LoginScene(this)));
    }
}

window.addEventListener("load", () => new HGame());
