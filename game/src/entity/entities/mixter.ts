import {GameScene} from "../../scenes/game/gameScene";
import {EntityTypes} from "../entities";
import {Player} from "../player";
import {Poison} from "./poison";

import Sprite = Phaser.Physics.Arcade.Sprite;

export class Mixter extends Player {
    static THROW_POWER = 100.0;

    constructor(scene: GameScene, active: boolean) {
        super(scene, Mixter.createSprite(scene), active, EntityTypes.MIXTER);
    }

    static createSprite(scene: GameScene): Sprite {
        let sceneWidth = 1920;
        let conf = scene.config!;
        let x = (sceneWidth / (conf.playerCount + 1)) * (conf.playerIndex + 1);
        let sprite = scene.physics.add.sprite(x, 800, "santy").setScale(4);
        sprite.setFlipX(conf.playerIndex % 2 != 0);
        return sprite;
    }

    attack(callBack: any) {
        super.attack(callBack);
        this.onFrameOnce(2, () => {
            this.giveCloseAttackDamage();
        })
    }

    specialAttack(onComplete: () => void) {
        super.specialAttack(onComplete);
        this.energy -= this.specialAttackEnergy;
        if (this.active) {
            this.onFrameOnce(7, () => {
                const poison = EntityTypes.POISON.create(this.scene) as Poison;
                poison.x = this.x + Mixter.THROW_POWER * (this.isFacingLeft ? -1 : 1);
                poison.y = this.y + this.sprite.height * 0.75;
                poison.thrower = this;
                this.scene.entityRegistry.spawn(poison);
            })
        }
    }
}

