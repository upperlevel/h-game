import {Player} from "../player";
import {Poison} from "./poison";
import {EntityType} from "../entityType";
import {EntityTypes} from "../entityTypes";

import {World} from "../../world/world";
import {SpritesheetUtil} from "../../util/spritesheet";
import {Animator} from "../../util/animator";

import AnimatedSprite = PIXI.extras.AnimatedSprite;

export class SantyType extends EntityType {
    id = "santy";

    constructor() {
        super();

        const texture = "assets/game/santy.png";

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
            () => SpritesheetUtil.horizontal(PIXI.utils.TextureCache[texture], 48, 48, 3, 9),
            (sprite: AnimatedSprite) => {
                sprite.animationSpeed = 0.1;
                sprite.loop = false;
            }
        ));
    }

    create(world: World, active: boolean) {
        return new Santy(world, active);
    }
}

export class Santy extends Player {
    static THROW_POWER = 2.0;

    constructor(world: World, active: boolean) {
        super(world, Player.createBody(world), active, EntityTypes.SANTY);
    }

    attack(onComplete: () => void) {
        super.attack(onComplete);
        this.sprite.onFrameChange = (frame: number) => {
            if (frame == 2) {
                this.giveCloseAttackDamage();
            }
        };
    }

    specialAttack(onComplete: () => void) {
        super.specialAttack(onComplete);
        this.energy -= this.specialAttackEnergy;
        if (this.active) {
            this.sprite.onFrameChange = (frame: number) => {
                if (frame == 7) {
                    const poison = EntityTypes.POISON.create(this.world) as Poison;
                    poison.x = this.x + Santy.THROW_POWER * (this.isFacingLeft ? -1 : 1);
                    poison.y = this.y + this.sprite.height * 0.75;
                    poison.thrower = this;
                    this.world.spawn(poison);
                }
            };
        }
    }
}

