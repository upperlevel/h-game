import {GameScene} from "../scenes/game/gameScene";
import {EntityResetPacket, EntitySpawnPacket, GamePacket} from "../protocol";
import {Entity} from "./entity";
import {EntityTypes} from "./entities";

export class EntityRegistry {
    entities = new Map<number, Entity>();

    private playerCount = 2;
    private localOffset = 0;

    private localId = 0;

    private timerId: any;

    readonly scene: GameScene;

    constructor(scene: GameScene) {
        this.scene = scene;
    }

    onEnable() {
        this.timerId = window.setTimeout(this.sendReset.bind(this), 1000);
    }

    onDisable() {
        window.clearTimeout(this.timerId);
    }

    onSpawn(packet: EntitySpawnPacket) {
        let type = EntityTypes.fromId(packet.entityType);

        if (type == null) {
            console.log("Invalid spawn: entity type " + packet.entityType + " not defined");
            return
        }

        let entity = type.create(this.scene);
        entity.id = packet.entityId;
        entity.sprite.setX(packet.x);
        entity.sprite.setY(packet.y);
        entity.sprite.setFlipX(packet.isFacingLeft);
        if (packet.meta != null) {
            entity.loadSpawnMeta(packet.meta);
        }
    }

    sendReset() {
        for (let [id, entity] of this.entities) {
            if (!entity.active) continue;
            entity.scene.sendPacket(this.createResetPacket(entity));
        }
    }

    private createResetPacket(entity: Entity) {
        let resetPacket: EntityResetPacket = {
            type: "entity_reset",
            entityId: entity.id
        };
        entity.fillResetPacket(resetPacket);
        return resetPacket;
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

        let packet: EntitySpawnPacket = {
            type: "entity_spawn",
            entityType: entity.type.id,
            entityId: entity.id,
            x: entity.body.x,
            y: entity.body.y,
            isFacingLeft: entity.sprite.flipX,
            meta: entity.createSpawnMeta(),
        };
        entity.scene.sendPacket(packet);
    }


    onUpdate(timedelta: number): any {
        for (let [id, entity] of this.entities) {
            entity.update(timedelta);
        }
    }
}
