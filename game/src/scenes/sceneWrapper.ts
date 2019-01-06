import * as Phaser from "phaser";
import Scene = Phaser.Scene;

import {HGame} from "../index";


export abstract class SceneWrapper extends Scene {
    id: string;

    // Would give a TypeScript compilation error but we ignore it.
    // We just need it to define the type HGame for game variable.
    // @ts-ignore
    game: HGame;

    protected constructor(key: string) {
        super({key: key});
        this.id = key;
    }

    init() {
        this.events.once("shutdown", () => this.shutdown());
    }

    preload() {
        console.log(`Loading scene: ${this.id}`);
        this.onPreload();
    }

    abstract onPreload(): void;

    create() {
        console.log(`Creating scene: ${this.id}`);
        this.onCreate();
    }

    abstract onCreate(): void;

    update(time: number, delta: number) {
        this.onUpdate(time, delta);
    }

    abstract onUpdate(time: number, delta: number): void;

    shutdown() {
        console.log(`Shutting down scene: ${this.id}`);
        this.onShutdown();
    }

    abstract onShutdown(): void;

    changeScene(next: string) {
        this.scene.start(next);
    }
}
