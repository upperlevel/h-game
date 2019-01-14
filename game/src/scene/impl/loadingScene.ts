import {Scene} from "../scene";
import {SceneManager} from "../sceneManager";

import Loader = PIXI.loaders.Loader;
import Resource = PIXI.loaders.Resource;

// TODO make overlay
export class LoadingScene implements Scene {
    manager: SceneManager;
    urls: string[];
    next?: Scene;

    constructor(manager: SceneManager, urls: string[], next?: Scene) {
        this.manager = manager;
        this.urls = urls;
        this.next = next;
    }

    onProgress(loader: Loader, resource: Resource) {
        console.log(`Loading: ${resource.url}`);
        console.log(`Progress: ${loader.progress}%`);
    }

    onDone() {
        this.manager.setScene(this.next);
    }

    enable() {
        PIXI.loader
            .add(this.urls)
            .on("progress", this.onProgress.bind(this))
            .load(this.onDone.bind(this));
    }

    update(delta: number) {
    }

    disable() {
    }
}
