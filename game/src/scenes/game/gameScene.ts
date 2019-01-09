import {SceneWrapper} from "../sceneWrapper"

import {Keyboard} from "../../actions";
import {EntityRegistry} from "../../entity/entityRegistry";
import {EntityTypes} from "../../entity/entities";
import {GamePacket} from "../../protocol";
import {GameConnector} from "../../connector/gameConnector";

export class GameScene extends SceneWrapper {
    // @ts-ignore
    entityRegistry: EntityRegistry;
    // @ts-ignore
    actions: Keyboard;

    // @ts-ignore
    relay: GameConnector;

    constructor() {
        super({key: "game"});
    }

    onInit(data: any) {
    }

    onPreload() {
        EntityTypes.preload(this);
    }

    onPacket(packet: GamePacket) {
        switch (packet.type) {
            case "entity_spawn":
                this.entityRegistry.onSpawn(packet);
                break;
            default:
                console.error(`Unhandled packet type: ${packet.type}`);
                break;
        }
    }

    onCreate() {
        this.relay = this.game.gameConnector!;
        this.relay.events.on("message", this.onPacket, this);

        this.actions = new Keyboard(this);

        EntityTypes.load(this);


        this.entityRegistry = new EntityRegistry(this);

        let santy = EntityTypes.SANTY.create(this);
        this.entityRegistry.spawn(santy);
    }

    onUpdate(time: number, delta: number) {
        this.entityRegistry!.onUpdate(delta);
    }

    onShutdown() {
        this.relay.events.removeListener("message", this.onPacket, this, false);
    }

    sendPacket(packet: GamePacket) {
        this.relay.send(packet);
    }
}
