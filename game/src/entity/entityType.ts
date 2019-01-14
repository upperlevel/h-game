import {GameScene} from "../scene/game/gameScene";
import {Entity} from "./entity";

export class EntityType {
    id: string;

    preloader: (scene: Scene) => void;
    loader: (scene: Scene) => void;

    animations: { [key: string]: string };

    creator: (scene: GameScene, active: boolean) => Entity;

    constructor(
        id: string,
        preloader: (scene: Scene) => void,
        loader: (scene: Scene) => void,
        animations: { [key: string]: string },
        creator: (scene: GameScene, active: boolean) => Entity
    ) {
        this.id = id;
        this.preloader = preloader;
        this.loader = loader;
        this.animations = animations;
        this.creator = creator;
    }

    create(scene: GameScene, active: boolean = true): Entity {
        return this.creator(scene, active);
    }
}
