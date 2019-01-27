import {Player, PlayerConfig} from "../player/player";
import {Poison} from "./poison";

import {World} from "../../world/world";
import {EntityType} from "../entityType";
import {Animator} from "../../util/animator";
import {SpritesheetUtil} from "../../util/spritesheet";
import {CloseRangeAttack} from "../player/closeRangeAttack";
import AnimatedSprite = PIXI.extras.AnimatedSprite;
import {EntityTypes} from "../entityTypes";
import {Mikrotik} from "./mikrotik";

export class MixterType extends EntityType {
    id = "mixter";

    constructor() {
        super();

        const texture = "assets/game/mixter.png";

        this.addAsset(texture);

        this.addAnimator(new Animator(
            "idle",
            () => SpritesheetUtil.horizontal(PIXI.utils.TextureCache[texture], 48, 48, 0, 2),
            (sprite: AnimatedSprite) => {
                sprite.animationSpeed = 0.1;
                sprite.loop = true;
            }
        ));

        this.addAnimator(new Animator(
            "walk",
            () => SpritesheetUtil.horizontal(PIXI.utils.TextureCache[texture], 48, 48, 1, 3),
            (sprite: AnimatedSprite) => {
                sprite.animationSpeed = 0.1;
                sprite.loop = true;
            }
        ));

        this.addAnimator(new Animator(
            "attack",
            () => SpritesheetUtil.horizontal(PIXI.utils.TextureCache[texture], 48, 48, 2, 3),
            (sprite: AnimatedSprite) => {
                sprite.animationSpeed = 0.1;
                sprite.loop = false;
            }
        ));

        this.addAnimator(new Animator(
            "specialAttack",
            () => SpritesheetUtil.horizontal(PIXI.utils.TextureCache[texture], 48, 48, 3, 3),
            (sprite: AnimatedSprite) => {
                sprite.animationSpeed = 0.1;
                sprite.loop = false;
            }
        ));
    }

    create(world: World, active: boolean, config?: PlayerConfig) {
        return new Mixter(world, active, config || {});
    }
}

export class Mixter extends Player {
    static THROW_POWER = 2.0;


    closeAttack = new CloseRangeAttack(this);

    constructor(world: World, active: boolean, config: PlayerConfig) {
        super(world, Player.createBody(world), active, EntityTypes.MIXTER, config);
    }

    attack(callBack: any) {
        super.attack(callBack);
        this.onFrameOnce(1, () => {
            for (const entity of this.closeAttack.getContacts()) {
                entity.damage(this.attackPower);
            }
        })
    }

    specialAttack(onComplete: () => void) {
        super.specialAttack(onComplete);
        this.energy -= this.specialAttackEnergy;
        if (this.active) {
            this.onFrameOnce(2, () => {
                const mikrotik = EntityTypes.MIKROTIK.create(this.world, true) as Mikrotik;
                mikrotik.x = this.x + Mixter.THROW_POWER * (this.flipX ? -1 : 1);
                mikrotik.y = this.y + this.sprite.height * 0.75;
                mikrotik.thrower = this;
                this.world.spawn(mikrotik);
            });
        }
    }
}

