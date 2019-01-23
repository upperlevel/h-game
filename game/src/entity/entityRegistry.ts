import {BehaviourChangePacket, EntityResetPacket, EntitySpawnPacket} from "../protocol";
import {Entity} from "./entity";
import {World} from "../world/world";
import {EntityTypes} from "./entityTypes";

export class EntityRegistry {
    entities = new Map<number, Entity>();

    private playerCount = 0;
    private localOffset = 0;

    private localId = 0;

    private timerId: any;

    readonly world: World;

    constructor(world: World) {
        this.world = world;
    }

    onEnable() {
        this.timerId = window.setInterval(this.sendReset.bind(this), 1000);
    }

    onDisable() {
        window.clearTimeout(this.timerId);
    }

    setup(playerCount: number, playerIndex: number) {
        this.playerCount = playerCount;
        this.localOffset = playerIndex;
    }

    onSpawn(packet: EntitySpawnPacket) {
        let type = EntityTypes.get(packet.entityType);

        if (type == null) {
            console.log("Invalid spawn: entity type " + packet.entityType + " not defined");
            return
        }

        let entity = type.create(this.world, false);
        entity.id = packet.entityId;
        entity.sprite.x = packet.x;
        entity.sprite.y = packet.y;
        entity.sprite.scale.x = packet.isFacingLeft ? -1 : 1;
        if (packet.meta != null) {
            entity.loadSpawnMeta(packet.meta);
        }

        this.forceSpawn(entity);
    }

    onBehaviourChange(packet: BehaviourChangePacket) {
        const entity: any = this.entities.get(packet.actorId);

        // If the entity supports behaviours.
        if (entity.behaviour != null) {
            const layer = entity.behaviour.layers[packet.layerIndex];
            if (layer == null) {
                throw `Layer ${packet.layerIndex} is null for entity: ${packet.actorId}`;
            }

            const behaviour = layer.behaviours.get(packet.behaviour);
            if (behaviour == null) {
                throw `Behaviour ${packet.behaviour} is null for entity: ${packet.actorId}`;
            }

            layer.active = behaviour;
        } else {
            throw `Entity ${packet.actorId} does not support behaviours`
        }
    }

    sendReset() {
        for (let [id, entity] of this.entities) {
            if (!entity.active) continue;
            entity.sendReset();
        }
    }

    onResetPacket(packet: EntityResetPacket) {
        let entity = this.entities.get(packet.entityId);
        if (entity == null) {
            throw `Entity ${packet.entityId} not found`;
        }
        entity.onReset(packet);
    }

    getEntity(id: number): Entity | undefined {
        return this.entities.get(id);
    }

    forceSpawn(entity: Entity) {
        if (entity.id == -1) {
            entity.id = this.localId++ * this.playerCount + this.localOffset;
        }

        this.world.app.stage.addChild(entity.sprite);
        this.entities.set(entity.id, entity);
    }

    spawn(entity: Entity) {
        if (entity.id != -1) return;
        this.forceSpawn(entity);

        let packet: EntitySpawnPacket = {
            type: "entity_spawn",
            entityType: entity.type.id,
            entityId: entity.id,
            x: entity.x,
            y: entity.y,
            isFacingLeft: entity.isFacingLeft,
            meta: entity.createSpawnMeta(),
        };
        entity.world.sendPacket(packet);
    }

    despawn(entity: Entity) {
        if (entity.id == -1) {
            console.warn("Trying to despawn a non-spawned entity!");
            return;
        }
        this.entities.delete(entity.id);
        entity.remove();
    }

    onPrePhysicsStep(timedelta: number) {
        for (let [id, entity] of this.entities) {
            entity.onPrePhysics(timedelta);
        }
    }

    onUpdate(timedelta: number): any {
        for (let [id, entity] of this.entities) {
            entity.onUpdate(timedelta);
        }
    }
}
