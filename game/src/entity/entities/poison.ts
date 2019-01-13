import {Player} from "../player";
import {GameScene} from "../../scenes/game/gameScene";
import {EntityTypes} from "../entities";
import {Entity} from "../entity";

import * as Phaser from "phaser";
import {ThrowableEntitySpawnMeta} from "../../protocol";
import Sprite = Phaser.Physics.Arcade.Sprite;

export class Poison extends Entity {
    _thrower?: Player;
    attacked = new Map<Entity, number>();
    attackForce = 25;
    attackTimeout = 500;

    constructor(scene: GameScene, active: boolean) {
        super(scene, Poison.createSprite(scene), active, EntityTypes.POISON);
        setTimeout(() => this.scene.entityRegistry.despawn(this), 5000);
    }

    get thrower(): Player {
        return this._thrower!;
    }

    set thrower(thrower: Player) {
        this._thrower = thrower;
        this.sprite.tint = thrower.active ? 0x00ff00 : 0xff0000;
    }

    createSpawnMeta(): ThrowableEntitySpawnMeta {
        return {
            type: "throwable",
            throwerEntityId: this.thrower!.id
        };
    }

    loadSpawnMeta(meta: ThrowableEntitySpawnMeta) {
        if (meta.type != "throwable") {
            throw Error("Error: invalid spawn meta type");
        }
        this.thrower = this.scene.entityRegistry.getEntity(meta.throwerEntityId) as Player;
    }

    static createSprite(scene: GameScene): Sprite {
        let sprite = scene.physics.add.sprite(0, 0, "poison").setScale(4);
        scene.physics.add.collider(sprite, scene.platformPhysicsGroup);
        let body = sprite.body as Phaser.Physics.Arcade.Body;
        body.setSize(body.width, 1);
        body.setOffset(0, 1);
        sprite.anims.play("poison_boil");
        return sprite;
    }

    private canAttack(player: Entity) {
        let lastTime = this.attacked.get(player);
        if (lastTime == null) return true;
        return Date.now() - lastTime >= this.attackTimeout;
    }

    private attack(player: Entity) {
        this.attacked.set(player, Date.now());
        player.damage(this.attackForce);
    }

    update(delta: number) {
        for (const entity of this.scene.entityRegistry.entities.values()) {
            if (entity == this.thrower) continue;

            // @ts-ignore
            if (!this.scene.physics.collide(this.sprite, entity.sprite)) continue;

            if (!this.canAttack(entity)) continue;

            this.attack(entity);
        }
    }
}

