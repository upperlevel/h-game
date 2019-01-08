import * as Phaser from "phaser";
import Scene = Phaser.Scene;
import Config = Phaser.Scenes.Settings.Config

import {HGame} from "../index";


export abstract class SceneWrapper extends Scene {
    id: string;

    // Would give a TypeScript compilation error but we ignore it.
    // We just need it to define the type HGame for game variable.
    // @ts-ignore
    game: HGame;

    protected constructor(config: Config) {
        super(config);
        this.id = config.key!;
    }

    init(config: any) {
        console.log(`Init scene: ${this.id} ${config != null ? config.toString() : ""}`);
        this.events.once("shutdown", () => this.shutdown());
        this.onInit(config);
    }

    onInit(config: any) {
    }

    preload() {
        console.log(`Loading scene: ${this.id}`);
        this.onPreload();
    }

    onPreload() {
    }

    create() {
        console.log(`Creating scene: ${this.id}`);
        this.onCreate();
    }

    onCreate() {
    }

    update(time: number, delta: number) {
        this.onUpdate(time, delta);
    }

    onUpdate(time: number, delta: number) {
    }

    shutdown() {
        console.log(`Shutting down scene: ${this.id}`);
        this.onShutdown();
    }

    onShutdown() {
    }

    changeScene(next: string, data: any = undefined) {
        this.scene.start(next, data);
    }
}
