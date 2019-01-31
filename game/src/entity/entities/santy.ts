import {Player, PlayerConfig} from "../player/player";
import {Poison} from "./poison";
import {EntityType} from "../entityType";
import {EntityTypes} from "../entityTypes";

import {World} from "../../world/world";
import {SpritesheetUtil} from "../../util/spritesheet";
import {Animator} from "../../util/animator";

import AnimatedSprite = PIXI.extras.AnimatedSprite;
import {CloseRangeAttack} from "../player/closeRangeAttack";
import {randomInArray} from "../../util/maths";

export class SantyType extends EntityType {
    id = "santy";

    constructor() {
        super();

        const texture = "assets/game/santy.png";

        this.addAsset(texture);

        this.addAnimator(new Animator("idle", texture)
            .grid({
                speed: 0.1,
                repeat: true,
                frames: {
                    width: 48,
                    height: 48,
                    list: [
                        {x: 0, y: 0},
                        {x: 1, y: 0},
                    ]
                }
            })
        );

        this.addAnimator(new Animator("walk", texture)
            .grid({
                speed: 0.1,
                repeat: true,
                frames: {
                    width: 48,
                    height: 48,
                    list: [
                        {x: 0, y: 1},
                        {x: 1, y: 1},
                        {x: 2, y: 1},
                    ]
                }
            })
        );

        this.addAnimator(new Animator("attack", texture)
            .grid({
                speed: 0.1,
                repeat: false,
                frames: {
                    width: 48,
                    height: 48,
                    list: [
                        {x: 0, y: 2},
                        {x: 1, y: 2},
                    ]
                }
            })
        );

        this.addAnimator(new Animator("specialAttack", texture)
            .grid({
                speed: 0.1,
                repeat: false,
                frames: {
                    width: 48,
                    height: 48,
                    list: [
                        {x: 0, y: 3},
                        {x: 1, y: 3},
                        {x: 2, y: 3},
                        {x: 3, y: 3},
                        {x: 4, y: 3},
                        {x: 5, y: 3},
                        {x: 6, y: 3},
                        {x: 7, y: 3},
                        {x: 8, y: 3},
                    ]
                }
            })
        );
    }

    create(world: World, active: boolean, config?: PlayerConfig) {
        return new Santy(world, active, config || {});
    }
}

export class Santy extends Player {
    static THROW_POWER = 2.0;

    closeAttack = new CloseRangeAttack(this);

    constructor(world: World, active: boolean, config: PlayerConfig) {
        super(world, Player.createBody(world), active, EntityTypes.SANTY, config);
    }

    attack(onComplete: () => void) {
        super.attack(onComplete);
        this.onFrameOnce(1, () => {
            for (const entity of this.closeAttack.getContacts()) {
                entity.damage(this.attackPower);
            }
        });
    }

    specialAttack(onComplete: () => void) {
        super.specialAttack(onComplete);
        this.energy -= this.specialAttackEnergy;
        if (this.active) {
            this.shoutComic(randomInArray([
                "Sacripante!",
                "Calderone\nRibollente!",
                "La pancia del\nCalcolatore!",
            ]));
            this.onFrameOnce(6, () => {
                const poison = EntityTypes.POISON.create(this.world, true) as Poison;
                poison.x = this.x + Santy.THROW_POWER * (this.flipX ? -1 : 1);
                poison.y = this.y + this.sprite.height * 0.75;
                poison.thrower = this;
                this.world.spawn(poison);
            });
        }
    }
}

