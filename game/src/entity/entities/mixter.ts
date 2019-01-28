import {Player, PlayerConfig} from "../player/player";

import {World} from "../../world/world";
import {EntityType} from "../entityType";
import {Animator} from "../../util/animator";
import {CloseRangeAttack} from "../player/closeRangeAttack";
import {EntityTypes} from "../entityTypes";
import {Mikrotik} from "./mikrotik";
import {toRadians} from "../../util/maths";
import {Entity} from "../entity";

export class MixterType extends EntityType {
    id = "mixter";

    constructor() {
        super();

        const texture = "assets/game/mixter.png";

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
                        {x: 2, y: 2},
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
                    ]
                }
            })
        );
    }

    create(world: World, active: boolean, config?: PlayerConfig) {
        return new Mixter(world, active, config || {});
    }
}

export class Mixter extends Player {
    static THROW_POWER = 8.0;

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

    private throw(entity: Entity) {
        let power = Mixter.THROW_POWER;
        let angle = toRadians(35);

        // After the entity has been spawned we apply the impulse.
        let powerX = Math.cos(angle) * power;
        let powerY = Math.sin(angle) * power;
        powerX = this.flipX ? -powerX : powerX;

        const vel = this.body.getLinearVelocity();
        entity.applyImpulse(powerX + vel.x, powerY + vel.y, this.x, this.y + this.height/2, true);
    }

    specialAttack(onComplete: () => void) {
        super.specialAttack(onComplete);
        this.energy -= this.specialAttackEnergy;
        if (this.active) {
            this.onFrameOnce(2, () => {
                const mikrotik = EntityTypes.MIKROTIK.create(this.world, true) as Mikrotik;

                mikrotik.x = this.x + this.width / 4 * (!this.left ? 1 : -1);
                mikrotik.y = this.y + this.sprite.height * 0.5;
                mikrotik.thrower = this;
                this.world.spawn(mikrotik);
                this.throw(mikrotik);
            });
        }
    }
}

