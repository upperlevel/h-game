import {GameScene} from "../../scenes/game/gameScene";
import {EntityTypes} from "../entities";
import {Player} from "../player";
import {Poison} from "./poison";

import Sprite = Phaser.GameObjects.Sprite;
import Scene = Phaser.Scene;

export class Santy extends Player {
    static THROW_POWER = 100.0;

    constructor(scene: GameScene, active: boolean) {
        super(scene, Santy.createSprite(scene), active, EntityTypes.SANTY);
    }

    static createSprite(scene: Scene): Sprite {
        let sprite = scene.physics.add.sprite(200, 200, "santy").setScale(4);
        sprite.setCollideWorldBounds(true);
        return sprite;
    }

    specialAttack(onComplete: () => void) {
        super.specialAttack(() => {
            const poison: Poison = EntityTypes.POISON.create(this.scene);
            poison.x = this.x + Santy.THROW_POWER * (this.isFacingLeft ? -1 : 1);
            poison.y = this.y;
            poison.thrower = this;

            onComplete();

            setTimeout(() => this.scene.entityRegistry.despawn(poison), 5000);
        });
    }
}

