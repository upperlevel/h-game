import {Player} from "../player/player";
import {Entity} from "../entity";

import {ThrowableEntitySpawnMeta} from "../../protocol";
import {World} from "../../world/world";
import {EntityType} from "../entityType";
import {Animator} from "../../util/animator";
import {SpritesheetUtil} from "../../util/spritesheet";
import {EntityTypes} from "../entityTypes";
// @ts-ignore
import * as planck from "planck-js";
import AnimatedSprite = PIXI.extras.AnimatedSprite;

export class PoisonType extends EntityType {
    id = "poison";

    static frameWidth = 37;
    static frameHeight = 5;

    static scale = 2 / 48;

    width = PoisonType.frameWidth * PoisonType.scale;
    height = PoisonType.frameHeight * PoisonType.scale;

    constructor() {
        super();

        const texture = "assets/game/poison.png";

        this.addAsset(texture);

        this.addAnimator(new Animator(
            "boil",
            () => SpritesheetUtil.horizontal(PIXI.utils.TextureCache[texture], PoisonType.frameWidth, PoisonType.frameHeight, 0, 4),
            (sprite: AnimatedSprite) => {
                sprite.animationSpeed = 0.1;
                sprite.loop = true;
            }
        ));
    }

    create(world: World, active: boolean): Entity {
        return new Poison(world, active);
    }
}

export class Poison extends Entity {
    _thrower?: Player;
    attacked = new Map<Entity, number>();
    contacts = new Set<Entity>();

    attackForce = 25;
    attackTimeout = 500;

    constructor(world: World, active: boolean) {
        super(world, Poison.createBody(world), active, EntityTypes.POISON, "boil");
        setTimeout(() => this.world.despawn(this), 5000);
        this.setupFixtures();
    }

    private setupFixtures() {
        const w = EntityTypes.POISON.width;
        const h = EntityTypes.POISON.height;

        let shape = planck.Box(
            w / 2, h / 2,
            planck.Vec2(0, h * 3 / 2),
            0
        );

        this.body.createFixture({
            shape: shape,
            // Ignore player collisions
            filterCategoryBits: Player.COLLISION_CATEGORY,
            filterMaskBits: ~Player.COLLISION_CATEGORY,
        });
        this.body.createFixture({
            shape: shape,
            // Collide ONLY with players
            filterMaskBits: Player.COLLISION_CATEGORY,
            isSensor: true,
            userData: {
                onTouchBegin: (f: planck.Fixture) => {
                    this.onPlayerContactBegin(f.getUserData() as Player);
                },
                onTouchEnd: (f: planck.Fixture) => {
                    this.onPlayerContactEnd(f.getUserData());
                },
            },
        });
    }

    get thrower(): Player {
        return this._thrower!;
    }

    set thrower(thrower: Player) {
        this._thrower = thrower;
        this.sprite.tint = thrower.active ? 0x00ff00 : 0xff0000;
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
        return world.physics.createBody({
            type: "dynamic"
        });
    }

    private canAttack(player: Entity) {
        let lastTime = this.attacked.get(player);
        if (lastTime == null) return true;
        return Date.now() - lastTime >= this.attackTimeout;
    }

    private attack(player: Entity) {
        this.attacked.set(player, Date.now());
        player.damage(this.attackForce);
    }

    onPlayerContactBegin(entity: Player) {
        if (entity == this.thrower) return;
        this.contacts.add(entity);
    }

    onPlayerContactEnd(entity: Player) {
        if (entity == this.thrower) return;
        this.contacts.delete(entity);
    }

    onUpdate(delta: number) {
        super.onUpdate(delta);
        for (const entity of this.contacts) {
            if (!this.canAttack(entity)) continue;

            this.attack(entity);
        }
    }
}

