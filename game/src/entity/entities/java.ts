import {Player, PlayerConfig} from "../player/player";
import {EntityType} from "../entityType";
import {EntityTypes} from "../entityTypes";

import {World} from "../../world/world";
import {Animator} from "../../util/animator";
import {Entity} from "../entity";

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
                repeat: false,
                frames: {
                    width: 48,
                    height: 48,
                    list: [
                        {x: 0, y: 2},
                        {
                            x: 1,
                            y: 2,
                            on(entity: Entity) {
                                entity.createEmitter("laser", {
                                    x: 34 / 48 * entity.width,
                                    y: 22 / 48 * entity.height,
                                    scale: 0.25,
                                    textures: ["assets/game/particle.png"],
                                    config: PIXI.loader.resources["assets/game/laser.json"].data,
                                })
                            }
                        },
                        {
                            x: 2,
                            y: 2,
                            on(entity: Entity) {
                                entity.getEmitter("laser").container.position.set(
                                    39 / 48 * entity.width,
                                    20 / 48 * entity.height
                                );
                            }
                        },
                        {
                            x: 3,
                            y: 2,
                            on(entity: Entity) {
                                entity.getEmitter("laser").container.position.set(
                                    46 / 48 * entity.width,
                                    25 / 48 * entity.height
                                );
                            }
                        },
                        {
                            x: 4,
                            y: 2,
                            on(entity: Entity) {
                                entity.removeEmitter("laser");
                            }
                        },
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
