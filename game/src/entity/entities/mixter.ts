import {EntityTypes} from "../entities";
import {Player} from "../player/player";
import {Poison} from "./poison";

import {World} from "../../world/world";

export class Mixter extends Player {
    static THROW_POWER = 100.0;

    constructor(world: World, active: boolean) {
        super(world, Player.createBody(world), active, EntityTypes.MIXTER);
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
                const poison = EntityTypes.POISON.create() as Poison;
                poison.x = this.x + Mixter.THROW_POWER * (this.flipX ? -1 : 1);
                poison.y = this.y + this.sprite.height * 0.75;
                poison.thrower = this;
                this.world.spawn(poison);
            })
        }
    }
}

