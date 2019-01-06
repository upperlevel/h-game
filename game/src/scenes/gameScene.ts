import {SceneWrapper} from "./sceneWrapper"

import {Keyboard} from "../actions";
import {EntityRegistry} from "../entity/entityRegistry";
import {EntityTypes} from "../entity/entities";
import {GamePacket} from "../protocol";

export class GameScene extends SceneWrapper {
    // @ts-ignore
    entityRegistry: EntityRegistry;
    // @ts-ignore
    actions: Keyboard;

    socket?: WebSocket;

    constructor() {
        super("game");
    }

    onPreload() {
        EntityTypes.preload(this);
    }

    onCreate() {
        this.actions = new Keyboard(this);

        EntityTypes.load(this);


        this.entityRegistry = new EntityRegistry(this);

        let santy = EntityTypes.SANTY.create(this);
        this.entityRegistry.spawn(santy);

        //this.cameras.main.startFollow(santy.sprite, true, 0.02, 0.02)
    }

    onUpdate(time: number, delta: number) {
        this.entityRegistry!.onUpdate(delta);
    }

    onShutdown() {
    }

    sendPacket(packet: GamePacket) {
        if (this.socket != null) {
            this.socket.send(JSON.stringify(packet))
        }
    }
}
