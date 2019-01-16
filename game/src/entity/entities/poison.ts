import {Player} from "../player";
import {EntityTypes} from "../entities";
import {Entity} from "../entity";

import {ThrowableEntitySpawnMeta} from "../../protocol";
import {World} from "../../world";

export class Poison extends Entity {
    _thrower?: Player;
    attacked = new Map<Entity, number>();
    contacts = new Array<Entity>();

    attackForce = 25;
    attackTimeout = 500;

    constructor(world: World, active: boolean) {
        super(world, Poison.createBody(world), active, EntityTypes.POISON);
        setTimeout(() => this.world.despawn(this), 5000);
        this.setupPlayerSensor();
    }

    private setupPlayerSensor() {
        this.body.getFixtureList()!.getNext()!.setUserData({
            onContactBegin: (f: planck.Fixture) => {
                let data = f.getUserData();
                if (data instanceof Player) {
                    this.onPlayerContactBegin(f.getUserData() as Player);
                }
            },
            onContactEnd: (f: planck.Fixture) => {
                let data = f.getUserData();
                if (data instanceof Player) {
                    this.onPlayerContactEnd(data);
                }
            },
        })
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
        //let sprite = scene.physics.add.sprite(0, 0, "poison").setScale(4);
        let body = world.physics.createBody({
            type: "dynamic"
        });

        const w = 37;

        let shape = planck.Edge(
            planck.Vec2(-w / 2, 0),
            planck.Vec2(+w / 2, 0.2),
        );

        body.createFixture({
            shape: shape,
            // Ignore player collisions
            filterCategoryBits: Player.COLLISION_CATEGORY,
            filterMaskBits: ~Player.COLLISION_CATEGORY,
        });
        body.createFixture({
           shape: shape,
            // Collide ONLY with players
            filterMaskBits: Player.COLLISION_CATEGORY,
            isSensor: true,
        });

        return body;
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
        this.contacts.push(entity);
    }

    onPlayerContactEnd(entity: Player) {
        if (entity == this.thrower) return;
        this.contacts.slice(this.contacts.indexOf(entity), 1);
    }

    update(delta: number) {
        for (const entity of this.contacts) {
            if (!this.canAttack(entity)) continue;

            this.attack(entity);
        }
    }
}

