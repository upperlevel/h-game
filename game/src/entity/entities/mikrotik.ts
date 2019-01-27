import {Player} from "../player/player";
import {Entity} from "../entity";

import {ThrowableEntitySpawnMeta} from "../../protocol";
import {World} from "../../world/world";
import {EntityType} from "../entityType";
import {Animator} from "../../util/animator";
import {SpritesheetUtil} from "../../util/spritesheet";
import AnimatedSprite = PIXI.extras.AnimatedSprite;
import {EntityTypes} from "../entityTypes";

// @ts-ignore
//import * as planck from "planck-js";
import {Emitter} from "../../world/emitter";
import * as planck from "planck-js";

export class MikrotikType extends EntityType {
    id = "mikrotik";

    static frameWidth = 10;
    static frameHeight = 8;

    static scale = 2 / 48;

    width = MikrotikType.frameWidth * MikrotikType.scale;
    height = MikrotikType.frameHeight * MikrotikType.scale;

    constructor() {
        super();

        const texture = "assets/game/mikrotik.png";

        this.addAsset(texture);
        this.addAsset("assets/game/particle.png");

        this.addAnimator(new Animator(
            "blink",
            () => SpritesheetUtil.horizontal(PIXI.utils.TextureCache[texture], MikrotikType.frameWidth, MikrotikType.frameHeight, 0, 4),
            (sprite: AnimatedSprite) => {
                sprite.animationSpeed = 0.1;
                sprite.loop = true;
            }
        ));
    }

    create(world: World, active: boolean): Entity {
        return new Mikrotik(world, active);
    }
}

export class Mikrotik extends Entity {
    static MAX_EXPLOSION_DISTANCE = 4;
    static MAX_EXPLOSION_DAMAGE = 100;

    _thrower?: Player;

    attackForce = 25;

    constructor(world: World, active: boolean) {
        super(world, Mikrotik.createBody(world), active, EntityTypes.MIKROTIK, "blink");
        setTimeout(() => this.explode(), 1000);
    }

    get thrower(): Player {
        return this._thrower!;
    }

    set thrower(thrower: Player) {
        this._thrower = thrower;
    }

    explode() {
        this.world.despawn(this);

        const mikLoc = this.body.getWorldCenter();
        const mikX = mikLoc.x;
        const mikY = mikLoc.y;
        const maxDist = Mikrotik.MAX_EXPLOSION_DISTANCE;
        for (const [id, entity] of this.world.entities) {
            if (!entity.damageable) continue;
            const entLoc = entity.body.getWorldCenter();
            const dist = Math.sqrt((mikX - entLoc.x) ** 2 + (mikY - entLoc.y) ** 2);

            if (dist > maxDist) continue;

            entity.damage((maxDist - dist) / maxDist * Mikrotik.MAX_EXPLOSION_DAMAGE);
        }

        const conf = {
            "alpha": {
                "start": 0.8,
                "end": 0.1
            },
            "scale": {
                "start": 1,
                "end": 0.3,
                "minimumScaleMultiplier": 1
            },
            "color": {
                "start": "#fb1010",
                "end": "#f5b830"
            },
            "speed": {
                "start": 200,
                "end": 100,
                "minimumSpeedMultiplier": 2.27
            },
            "acceleration": {
                "x": 0,
                "y": 0
            },
            "maxSpeed": 0,
            "startRotation": {
                "min": 0,
                "max": 360
            },
            "noRotation": true,
            "rotationSpeed": {
                "min": 0,
                "max": 0
            },
            "lifetime": {
                "min": 0.4,
                "max": 0.5
            },
            "blendMode": "normal",
            "frequency": 0.001,
            "emitterLifetime": 0.15,
            "maxParticles": 1000,
            "pos": {
                "x": 0,
                "y": 0
            },
            "addAtBack": false,
            "spawnType": "point"
        };
        this.world.createEmitter({
            x: this.x,
            y: this.y,
            textures: ["assets/game/particle.png"],
            scale: 1,
            config: conf,
        });
    }

    createSpawnMeta(): ThrowableEntitySpawnMeta {
        return {
            type: "throwable",
            throwerEntityId: this.thrower!.id
        };
    }

    loadSpawnMeta(meta: ThrowableEntitySpawnMeta) {
        if (meta.type != "throwable") {
            throw Error("Error: invalid spawn meta type");
        }
        this.thrower = this.world.entityRegistry.getEntity(meta.throwerEntityId) as Player;
    }

    static createBody(world: World): planck.Body {
        let body = world.physics.createBody({
            type: "dynamic"
        });

        const w = EntityTypes.POISON.width;
        const h = EntityTypes.POISON.height;

        body.createFixture({
            shape: planck.Box(
                w / 2, h / 2,
                planck.Vec2(0, h / 2),
                0
            ),
            // Ignore player collisions
            filterCategoryBits: Player.COLLISION_CATEGORY,
            filterMaskBits: ~Player.COLLISION_CATEGORY,
        });

        return body;
    }

    onUpdate(delta: number) {
        super.onUpdate(delta);
    }
}

