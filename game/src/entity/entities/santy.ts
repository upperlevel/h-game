import {GameScene} from "../../scenes/game/gameScene";
import {EntityTypes} from "../entities";
import {Player} from "../player";
import {Poison} from "./poison";

import Sprite = Phaser.Physics.Arcade.Sprite;
import Scene = Phaser.Scene;

export class Santy extends Player {
    static THROW_POWER = 100.0;

    constructor(scene: GameScene, active: boolean) {
        super(scene, Santy.createSprite(scene), active, EntityTypes.SANTY);
    }

    static createSprite(scene: Scene): Sprite {
        return scene.physics.add.sprite(200, 800, "santy").setScale(4);
    }

    specialAttack(onComplete: () => void) {
        super.specialAttack(() => {
            if (this.active) {
                const poison = EntityTypes.POISON.create(this.scene) as Poison;
                poison.x = this.x + Santy.THROW_POWER * (this.isFacingLeft ? -1 : 1);
                poison.y = this.y;
                poison.thrower = this;
                this.scene.entityRegistry.spawn(poison);
            }

            onComplete();
        });
    }
}

