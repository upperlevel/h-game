import {EntityResetPacket} from "../protocol"
import {GameScene} from "../scenes/gameScene";
import Sprite = Phaser.GameObjects.Sprite;


export abstract class Entity {
    id = -1;
    type: EntityType;

    sprite: Sprite;

    maxLife = 1.0;
    life = this.maxLife;
    damageable = false;

    destroyed = false;

    active = false;

    constructor(sprite: Sprite, active: boolean, type: EntityType) {
        this.sprite = sprite;
        this.active = active;
        this.type = type;
    }

    update(deltatime: number) {
    }

    get body(): Phaser.Physics.Arcade.Body {
        if (this.sprite == null) throw new Error("sprite is null");
        if (this.sprite.body == null) throw new Error("body is null");
        return this.sprite.body as Phaser.Physics.Arcade.Body;
    }

    damage(amount: number) {
        if (!this.damageable) return;
        this.life -= amount;
        // TODO: Popup
    }

    // TODO: throw

    // TODO: spawnmeta

    fillResetPacket(packet: EntityResetPacket) {
        packet.x = this.body.x;
        packet.y = this.body.y;
    }

    onReset(packet: EntityResetPacket) {
        this.body.x = packet.x;
        this.body.y = packet.y;
    }
}

export class EntityRegistry {
    entities = new Map<number, Entity>();

    private playerCount = 2;
    private localOffset = 0;

    private localId = 0;

    private socket?: WebSocket;

    private timerId: any;

    onEnable() {
        this.timerId = window.setTimeout(this.sendReset.bind(this), 1000);
    }

    sendReset() {
        if (this.socket == null) return;
        for (let [id, entity] of this.entities) {
            if (!entity.active) continue;
            //TODO: this.socket.send(JSON.stringify(createResetPacket(entity)));
        }
    }

    onDisable() {
        window.clearTimeout(this.timerId);
    }

    getEntity(id: number): Entity | undefined {
        return this.entities.get(id);
    }

    forceSpawn(entity: Entity) {
        if (entity.id == -1) {
            entity.id = this.localId++ * this.playerCount + this.localOffset;
        }

        this.entities.set(entity.id, entity);
    }

    spawn(entity: Entity) {
        if (entity.id != -1) return;
        this.forceSpawn(entity);
        //TODO: notify other clients
    }


    onUpdate(timedelta: number): any {
        for (let [id, entity] of this.entities) {
            entity.update(timedelta);
        }
    }
}

export class EntityType {
    id: string;

    preloader: (scene: GameScene) => void;
    loader: (scene: GameScene) => void;

    animations: { [key: string]: string };


    creator: (scene: GameScene, active: boolean) => Entity;

    constructor(
        id: string,
        preloader: (scene: GameScene) => void,
        loader: (scene: GameScene) => void,
        animations: { [key: string]: string},
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



