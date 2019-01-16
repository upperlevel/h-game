import {EntityTypes} from "../entities";
import {Player} from "../player";
import {Poison} from "./poison";
import {EntityType} from "../entityType";

import AnimatedSprite = PIXI.extras.AnimatedSprite;
import Spritesheet = PIXI.Spritesheet;
import {World} from "../../world";

export class SantyType implements EntityType {
    id = "santy";

    spritesheetPath = "assets/images/santy.json";

    animations = {
        "idle": (sprite: AnimatedSprite, spritesheet: Spritesheet) => {
            sprite.textures = spritesheet.animations["santy_idle"];
            sprite.animationSpeed = 6;
            sprite.loop = true;
        },

        "walk": (sprite: AnimatedSprite, spritesheet: Spritesheet) => {
            sprite.textures = spritesheet.animations["santy_walk"];
            sprite.animationSpeed = 6;
            sprite.loop = true;
        },

        "attack": (sprite: AnimatedSprite, spritesheet: Spritesheet) => {
            sprite.textures = spritesheet.animations["santy_attack"];
            sprite.animationSpeed = 6;
            sprite.loop = false;
        },

        "specialAttack": (sprite: AnimatedSprite, spritesheet: Spritesheet) => {
            sprite.textures = spritesheet.animations["santy_special_attack"];
            sprite.animationSpeed = 6;
            sprite.loop = false;
        }
    };

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

