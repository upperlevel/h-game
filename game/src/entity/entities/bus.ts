import {EntityType} from "../entityType";
import {World} from "../../world/world";
import {Entity} from "../entity";
import {Animator} from "../../util/animator";
import {Player} from "../player/player";
import {EntityTypes} from "../entityTypes";

export class BusType extends EntityType {
    id = "bus";

    width = 4.75 * 1.5;
    height = 2.42 * 1.5;

    constructor() {
        super();

        const texture = "assets/game/bus.png";
        this.addAsset(texture);

        this.addAnimator(new Animator("going", texture)
            .grid({
                speed: 0.1,
                repeat: true,
                frames: {
                    width: 114,
                    height: 58,
                    list: [
                        {x: 0, y: 0},
                        {x: 1, y: 0},
                    ]
                }
            })
        )
    }

    create(world: World, active: boolean, config?: any): Bus {
        return new Bus(world, active);
    }
}

export class Bus extends Entity {
    constructor(world: World, active: boolean) {
        super(world, Player.createBody(world) /*todo*/, active, EntityTypes.BUS, "going");
    }
}
