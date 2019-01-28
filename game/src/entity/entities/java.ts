import {Player, PlayerConfig} from "../player/player";
import {Poison} from "./poison";
import {EntityType} from "../entityType";
import {EntityTypes} from "../entityTypes";

import {World} from "../../world/world";
import {SpritesheetUtil} from "../../util/spritesheet";
import {Animator, Step} from "../../util/animator";

import AnimatedSprite = PIXI.extras.AnimatedSprite;
import {CloseRangeAttack} from "../player/closeRangeAttack";

export class JavaType extends EntityType {
    id = "java";

    constructor() {
        super();

        const texture = "assets/game/java.png";

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
                repeat: true,
                frames: {
                    width: 48,
                    height: 48,
                    list: [
                        {x: 0, y: 2},
                        {x: 1, y: 2},
                        {x: 2, y: 2},
                        {x: 3, y: 2},
                        {x: 4, y: 2},
                    ]
                }
            })
        );

        this.addAnimator(new Animator("specialAttack", texture)
            .grid({
                speed: 0.1,
                repeat: true,
                frames: {
                    width: 48,
                    height: 48,
                    list: [
                        {x: 0, y: 3},
                        {x: 1, y: 3},
                        {x: 0, y: 3},
                        {x: 1, y: 3},
                        {x: 0, y: 3},
                        {x: 1, y: 3},
                        {x: 2, y: 3},
                        {x: 2, y: 3},
                    ]
                }
            })
        );
    }

    create(world: World, active: boolean, config?: PlayerConfig) {
        return new Java(world, active, config || {});
    }
}

export class Java extends Player {
    constructor(world: World, active: boolean, config: PlayerConfig) {
        super(world, Player.createBody(world), active, EntityTypes.JAVA, config);
    }
}
