import {SceneWrapper} from "../sceneWrapper"

import {Keyboard} from "../../actions";
import {EntityRegistry} from "../../entity/entityRegistry";
import {EntityTypes} from "../../entity/entities";
import {GamePacket} from "../../protocol";
import {GameConnector} from "../../connector/gameConnector";
import {GameSceneConfig} from "./gameSceneConfig";
import {Player} from "../../entity/player";

import {Position} from "../../entity/util"
import TileSprite = Phaser.GameObjects.TileSprite;
import Group = Phaser.GameObjects.Group;
import StaticGroup = Phaser.Physics.Arcade.StaticGroup;
import StaticBody = Phaser.Physics.Arcade.StaticBody;

export class GameScene extends SceneWrapper {
    // @ts-ignore
    entityRegistry: EntityRegistry;
    // @ts-ignore
    actions: Keyboard;

    // @ts-ignore
    relay: GameConnector;

    // @ts-ignore
    entityPhysicsGroup: Group;
    // @ts-ignore
    platformPhysicsGroup: StaticGroup;

    config?: GameSceneConfig;

    constructor() {
        super({key: "game"});
    }

    onInit(data: GameSceneConfig) {
        this.config = data;
    }

    onPreload() {
        EntityTypes.preload(this);
        this.load.image("urban_terrain", "assets/game/urban_terrain.png");
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
        this.cameras.main.setBounds(0, 0, 1920,1080);
        this.physics.world.setBounds(0, 0, 1920, 1080);

        EntityTypes.load(this);

        this.actions = new Keyboard(this);

        this.entityRegistry = new EntityRegistry(this);
        this.entityRegistry.setup(this.config!.playerCount, this.config!.playerIndex);
        this.entityRegistry.onEnable();

        this.entityPhysicsGroup = this.physics.add.group();

        // Setup terrain
        this.platformPhysicsGroup = this.physics.add.staticGroup();
        let ts = this.add.tileSprite(1920/2, 1080 - 25, 1920, 25, "urban_terrain");
        this.platformPhysicsGroup.add(ts);
        let body = ts.body as StaticBody;
        body.setOffset(0, 10);

        this.relay = this.game.gameConnector!;
        this.relay.subscribe("message", this.onPacket, this);

        // Spawn
        let santy = EntityTypes.SANTY.create(this) as Player;
        santy.name = this.config!.playerName;
        santy.reloadName();

        this.physics.add.collider(this.platformPhysicsGroup, this.entityPhysicsGroup);

        this.entityRegistry.spawn(santy);

        this.cameras.main.startFollow(santy.sprite);
    }

    onUpdate(time: number, delta: number) {
        this.entityRegistry.onUpdate(delta);
    }

    onShutdown() {
        this.entityRegistry.onDisable();
        this.relay.unsubscribe("message", this.onPacket, this, false);
    }

    sendPacket(packet: GamePacket) {
        this.relay.send(packet);
    }

    getSpawnLocation(): Position {
        return {
            x: Math.random() * 1920,
            y: 800,
        };
    }
}
