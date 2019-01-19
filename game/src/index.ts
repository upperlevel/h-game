import * as PIXI from "pixi.js"

import {SceneManager} from "./scene/sceneManager";

import {MatchmakingConnector} from "./connector/matchmakingConnector";
import {GameConnector} from "./connector/gameConnector";
import {ConnectingScene} from "./scene/impl/connectingScene";
import {LoginScene} from "./scene/impl/loginScene";
import {EntityTypes} from "./entity/entityTypes";

export class HGame {
    app: PIXI.Application;
    sceneManager: SceneManager;

    matchmakingConnector: MatchmakingConnector;
    gameConnector?: GameConnector;

    playerName?: string;

    constructor() {
        PIXI.settings.SCALE_MODE = PIXI.SCALE_MODES.NEAREST;

        this.app = new PIXI.Application();
        document.body.appendChild(this.app.view);

        this.fitCanvas();
        window.addEventListener("resize", this.fitCanvas.bind(this));

        this.matchmakingConnector = new MatchmakingConnector();

        this.sceneManager = new SceneManager();

        const loader = PIXI.loader;

        loader.onError.add((loader: PIXI.loaders.Loader, resource: PIXI.loaders.Resource) => {
            console.error(`Error ${resource.url}: ${resource.error}`);
        });

        loader.onLoad.add((loader: PIXI.loaders.Loader, resource: PIXI.loaders.Resource) => {
            console.log(`Loaded: ${resource.url}`);
        });

        loader.onComplete.add(() => {
            console.log(`Resources loading completed`);
        });

        loader
            .add(EntityTypes.getAssets())
            .add("assets/game/urban_terrain.png")
            .load(() => {
                EntityTypes.onLoad();

                this.sceneManager.setScene(new ConnectingScene(this.sceneManager, this.matchmakingConnector, new LoginScene(this)));
                console.log(`Loading process was completed.`);
            });
    }

    private fitCanvas() {
        // Makes the viewport fit the whole canvas
        this.app.renderer.resize(window.innerWidth, window.innerHeight);
    }
}

window.addEventListener("load", () => new HGame());
