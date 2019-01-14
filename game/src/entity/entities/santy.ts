import {GameScene} from "../../scene/game/gameScene";
import {EntityTypes} from "../entities";
import {Player} from "../player";
import {Poison} from "./poison";

import Sprite = Phaser.Physics.Arcade.Sprite;

export class Santy extends Player {
    static THROW_POWER = 2.0;

    constructor(scene: GameScene, active: boolean) {
        super(scene, Santy.createSprite(scene), active, EntityTypes.SANTY);
    }

    static createSprite(scene: GameScene): Sprite {
        let sprite = scene.physics.add.sprite(0, 4, "santy").setDisplaySize(Player.WIDTH, Player.HEIGHT);
        let conf = scene.config!;
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
                poison.x = this.x + Santy.THROW_POWER * (this.isFacingLeft ? -1 : 1);
                poison.y = this.y + this.sprite.height * 0.75;
                poison.thrower = this;
                this.scene.entityRegistry.spawn(poison);
            })
        }
    }
}

