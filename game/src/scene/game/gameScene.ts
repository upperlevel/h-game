import {SceneWrapper} from "../sceneWrapper"

import {Keyboard} from "../../actions";
import {EntityRegistry} from "../../entity/entityRegistry";
import {EntityTypes} from "../../entity/entities";
import {GamePacket} from "../../protocol";
import {GameConnector} from "../../connector/gameConnector";
import {GameSceneConfig} from "./gameSceneConfig";
import {Player} from "../../entity/player";

import {Popup} from "../../entity/popup";
import {Terrain} from "../../terrain/terrain"

export class GameScene extends SceneWrapper {
    // @ts-ignore
    entityRegistry: EntityRegistry;
    // @ts-ignore
    actions: Keyboard;

    // @ts-ignore
    relay: GameConnector;

    terrain?: Terrain;

    config?: GameSceneConfig;

    popups = new Set<Popup>();

    constructor() {
        super({key: "game"});
        this.terrain = new Terrain(this);
    }

    popup(popup: Popup) {
        this.popups.add(popup);
    }

    onInit(data: GameSceneConfig) {
        this.config = data;
    }

    onPreload() {
        this.terrain!.load();
        EntityTypes.preload(this);
    }

    onPacket(packet: GamePacket) {
        switch (packet.type) {
            case "entity_spawn":
                this.entityRegistry.onSpawn(packet);
                break;
            case "behaviour_change":
                this.entityRegistry.onBehaviourChange(packet);
                break;
            case "player_jump":
                let p = this.entityRegistry.getEntity(packet.entityId);
                if (p != null && 'jump' in p) {
                    // @ts-ignore
                    p.jump();
                }
                break;
            case "entity_reset":
                this.entityRegistry.onResetPacket(packet);
                break;
            default:
                console.error(`Unhandled packet type: ${packet.type}`);
                break;
        }
    }

    onCreate() {
        this.terrain!.build();

        EntityTypes.load(this);

        this.actions = new Keyboard(this);

        this.entityRegistry = new EntityRegistry(this);
        this.entityRegistry.setup(this.config!.playerCount, this.config!.playerIndex);
        this.entityRegistry.onEnable();

        this.relay = this.game.gameConnector!;
        this.relay.subscribe("message", this.onPacket, this);

        // Spawn
        let santy = this.config!.player.character!.create(this) as Player;
        santy.name = this.config!.playerName;
        santy.reloadName();

        this.entityRegistry.spawn(santy);
    }

    onUpdate(time: number, delta: number) {
        this.terrain!.update(delta);
        this.entityRegistry.onUpdate(delta);

        for (const popup of this.popups) {
            if (popup.update(delta)) {
                this.popups.delete(popup);
            }
        }
    }

    onShutdown() {
        this.entityRegistry.onDisable();
        this.relay.unsubscribe("message", this.onPacket, this, false);
    }

    sendPacket(packet: GamePacket) {
        this.relay.send(packet);
    }
}