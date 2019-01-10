import {Player} from "../player";
import {GameScene} from "../../scenes/game/gameScene";
import {EntityTypes} from "../entities";
import {Entity} from "../entity";

import * as Phaser from "phaser";
import Scene = Phaser.Scene;
import Sprite = Phaser.Physics.Arcade.Sprite;

export class Poison extends Entity {
    thrower?: Player;

    constructor(scene: GameScene, active: boolean) {
        super(scene, Poison.createSprite(scene), active, EntityTypes.POISON);
    }

    static createSprite(scene: GameScene): Sprite {
        let sprite = scene.physics.add.sprite(0, 0, "poison").setScale(4);
        scene.physics.add.collider(sprite, scene.platformPhysicsGroup);
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

