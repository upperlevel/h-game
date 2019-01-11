import {Player} from "../player";
import {GameScene} from "../../scenes/game/gameScene";
import {EntityTypes} from "../entities";
import {Entity} from "../entity";

import * as Phaser from "phaser";
import Sprite = Phaser.Physics.Arcade.Sprite;
import {ThrowableEntitySpawnMeta} from "../../protocol";

export class Poison extends Entity {
    thrower?: Player;

    constructor(scene: GameScene, active: boolean) {
        super(scene, Poison.createSprite(scene), active, EntityTypes.POISON);
        setTimeout(() => this.scene.entityRegistry.despawn(this), 5000);
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
        return sprite;
    }

    update(delta: number) {
        // TODO: currently poison instantly kills.
        for (const entity of this.scene.entityRegistry.entities.values()) {
            // @ts-ignore
            if (entity != this.thrower && this.scene.physics.collide(this.sprite, entity.sprite)) {
                entity.damage(500.0);
            }
        }
    }
}

