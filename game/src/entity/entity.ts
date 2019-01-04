import {EntityResetPacket} from "../protocol"
import {Behaviour} from "../behaviour/behaviour";
import Sprite = Phaser.Physics.Arcade.Sprite;


export abstract class Entity {
    id = -1;
    abstract type: string;

    abstract sprite: Sprite;

    maxLife = 1.0;
    life = this.maxLife;
    damageable = false;

    destroyed = false;

    active = false;

    update(deltatime: number) {
    }

    damage(amount: number) {
        if (!this.damageable) return;
        this.life -= amount;
        // TODO: Popup
    }

    // TODO: throw

    // TODO: spawnmeta

    fillResetPacket(packet: EntityResetPacket) {
        packet.x = this.sprite.body.x;
        packet.y = this.sprite.body.y;
    }

    onReset(packet: EntityResetPacket) {
        this.sprite.body.x = packet.x;
        this.sprite.body.y = packet.y;
    }
}

class EntityRegistry {
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
}

class EntityType {
    id: string;
    texturePath: string;

    creator: (active: boolean) => Entity;

    constructor(id: string, texturePath: string, creator: () => Entity) {
        this.id = id;
        this.texturePath = texturePath;
        this.creator = creator;
    }

    create(active: boolean = true): Entity {
        return this.creator(active);
    }
}

namespace EntityTypes {
    let types = [
        new EntityType("mikrotik", "mikrotik.jpg", null),
    ]
}


