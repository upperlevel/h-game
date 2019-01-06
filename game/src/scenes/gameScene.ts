import {SceneWrapper} from "./sceneWrapper"

import {Keyboard} from "../actions";
import {EntityRegistry} from "../entity/entity";
import {EntityTypes} from "../entity/entities";

export class GameScene extends SceneWrapper {
    // @ts-ignore
    entityRegistry: EntityRegistry;
    // @ts-ignore
    actions: Keyboard;

    constructor() {
        super("game");
    }

    onPreload() {
        EntityTypes.preload(this);
    }

    onCreate() {
        this.actions = new Keyboard(this);

        EntityTypes.load(this);


        this.entityRegistry = new EntityRegistry();

        let santy = EntityTypes.SANTY.create(this);
        this.entityRegistry.spawn(santy);
    }

    onUpdate(time: number, delta: number) {
        this.entityRegistry!.onUpdate(delta);
    }

    onShutdown() {
    }
}
