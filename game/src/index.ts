import * as PIXI from "pixi.js"

import {SceneManager} from "./scene/sceneManager";

import {MatchmakingConnector} from "./connector/matchmakingConnector";
import {GameConnector} from "./connector/gameConnector";
import {ConnectingScene} from "./scene/impl/connectingScene";
import {LoginScene} from "./scene/impl/loginScene";
import {EntityTypes} from "./entity/entityTypes";
import {GameScene} from "./scene/game/gameScene";
import {EntityType} from "./entity/entityType";

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

        const canvas = this.app.view;
        canvas.style.position = "fixed";
        canvas.style.top = "0";
        canvas.style.left = "0";

        this.onResize();
        window.addEventListener("resize", this.onResize.bind(this));

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
            .add("assets/game/grass.png")
            .add("assets/game/dirt.png")
            .add("assets/game/debug.png")
            .add("assets/game/tree.png")
            .load(() => {
                EntityTypes.onLoad();

                this.sceneManager.setScene(new ConnectingScene(this.sceneManager, this.matchmakingConnector, new LoginScene(this)));
                console.log(`Loading process was completed.`);
            });

        const ticker = new PIXI.ticker.Ticker();
        ticker.add((dt: number) => {
            this.sceneManager.update(ticker.elapsedMS / 1000);
        });
        ticker.start();
    }

    private onResize() {
        this.app.renderer.resize(window.innerWidth, window.innerHeight);

        if (this.sceneManager) {
            this.sceneManager.resize();
        }
    }
}

window.addEventListener("load", () => new HGame());
