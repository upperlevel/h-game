import * as Phaser from "phaser";
import Scene = Phaser.Scene;

export abstract class SceneWrapper extends Scene {
    id: string;

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

    update() {
        this.onUpdate();
    }

    abstract onUpdate(): void;

    shutdown() {
        console.log(`Shutting down scene: ${this.id}`);
        this.onShutdown();
    }

    abstract onShutdown(): void;

    changeScene(next: string) {
        this.scene.start(next);
    }
}
