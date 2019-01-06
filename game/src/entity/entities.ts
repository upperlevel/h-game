import {Santy} from "./player";
import {EntityType} from "./entity";
import {GameScene} from "../scenes/gameScene";

export namespace EntityTypes {
    import Scene = Phaser.Scene;

    export const SANTY: EntityType = new EntityType(
        "santy",
        (scene) => {
            scene.load.spritesheet("santy", "assets/game/santy.png", {frameWidth: 48, frameHeight: 48});
        },
        (scene: Scene) => {
            scene.anims.create({
                key: "santy_idle",
                frames: scene.anims.generateFrameNumbers("santy", {start: 0, end: 1}),
                frameRate: 6,
                repeat: -1,
            });
            scene.anims.create({
                key: "santy_walk",
                frames: scene.anims.generateFrameNumbers("santy", {start: 9, end: 11}),
                frameRate: 6,
                yoyo: true,
                repeat: -1,
            });
            scene.anims.create({
                key: "santy_attack",
                frames: scene.anims.generateFrameNumbers("santy", {start: 18, end: 19}),
                frameRate: 6,
                repeat: 0,
            });
            scene.anims.create({
                key: "santy_special_attack",
                frames: scene.anims.generateFrameNumbers("santy", {start: 27, end: 35}),
                frameRate: 6,
                repeat: 0,
            });
        },
        {
            "idle": "santy_idle",
            "walk": "santy_walk",
            "attack": "santy_attack",
            "special_attack": "santy_special_attack",
        },
        (scene: GameScene, active: boolean) => new Santy(scene, active)
    );


    export let types = [
        SANTY
    ];


    export function preload(scene: GameScene) {
        for (let type of types) {
            type.preloader(scene);
        }
    }

    export function load(scene: GameScene) {
        for (let type of types) {
            type.loader(scene);
        }
    }
}
