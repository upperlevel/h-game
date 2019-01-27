// @ts-ignore
import * as planck from "planck-js"
import * as Actions from "../../input/actions";

import {Entity} from "../entity";
import {createPlayerBehaviour} from "../../behaviour/behaviours";
import {BehaviourManager} from "../../behaviour/behaviour";
import {EntityResetPacket, PlayerEntitySpawnMeta} from "../../protocol";
import {World} from "../../world/world";
import {EntityType} from "../entityType";
import {HudRenderer} from "./hudRenderer";

export abstract class Player extends Entity {
    static SPRITE_SIZE = 48;

    static WIDTH  = 2;
    static HEIGHT = 2;
    static STEP_HEIGHT = (2.0 / Player.SPRITE_SIZE) * Player.HEIGHT;

    static COLLISION_CATEGORY = 0x2;

    maxEnergy = 1.0;
    energy = 0.0;
    specialAttackEnergy = 0.8;
    energyGainPerSec = 0.05;

    attackPower = 10;
    jumpForce = 40.0;

    name = "Ulisse";

    // override
    damageable = true;
    private behaviour: BehaviourManager;

    private hudRenderer: HudRenderer;

    get friction(): number {
        return this.body.getFixtureList()!.getFriction()
    }

    set friction(v: number) {
        this.body.getFixtureList()!.setFriction(v);
    }


    protected constructor(world: World, body: planck.Body, active: boolean, type: EntityType) {
        super(world, body, active, type);
        this.behaviour = createPlayerBehaviour(this);
        this.hudRenderer = new HudRenderer(world, this.name, this.active ? "lime" : "red");

        let x = 0; // (sceneWidth / (conf.playerCount + 1)) * (conf.playerIndex + 1);
        let y = 2;
        this.body.setPosition(planck.Vec2(x, y));
        this.body.getFixtureList()!.setUserData(this);

        let sensorW = Player.WIDTH / 2;
        let sensorH = 0.1;
        this.addSensor(body.createFixture({
            shape: planck.Box(
                sensorW / 2, sensorH / 2,
                planck.Vec2(0, -sensorH / 2),
                0
            ),
            isSensor: true,
        }));
    }

    onPrePhysics(timeDelta: number) {
        super.onPrePhysics(timeDelta);
        this.behaviour.onPrePhysics();
    }

    onUpdate(deltatime: number) {
        super.onUpdate(deltatime);

        this.behaviour.onUpdate();

        if (this.active && Actions.JUMP.pressed && this.isTouchingGround) {
            this.jump();
        }
        this.energy = Math.min(this.energy + this.energyGainPerSec * deltatime, this.maxEnergy);

        this.hudRenderer.update(this, this.life / this.maxLife, this.energy / this.maxEnergy);
    }

    reloadName() {
        this.hudRenderer.setName(this.name);
    }

    createSpawnMeta(): PlayerEntitySpawnMeta {
        return {
            type: "player",
            name: this.name,
        };
    }

    loadSpawnMeta(meta: PlayerEntitySpawnMeta) {
        if (meta.type != "player") {
            console.log("Error: invalid player meta type");
            return;
        }
        this.name = meta.name;
        this.reloadName();
    }

    jump() {
        this.body.applyLinearImpulse(planck.Vec2(0, this.jumpForce), this.body.getWorldCenter(), true);
        if (this.active) {
            this.world.sendPacket({
                type: "player_jump",
                entityId: this.id,
            })
        }
    }

    respawn() {
        super.respawn();
        this.energy = 0;
    }

    idle() {
        this.type.getAnimator("idle").bind(this.sprite);
        this.sprite.play();
    }

    attack(onComplete: () => void) {
        this.type.getAnimator("attack").bind(this.sprite);
        this.sprite.play();
        this.sprite.onComplete = onComplete;
    }

    canSpecialAttack(): boolean {
        return this.energy >= this.specialAttackEnergy;
    }

    specialAttack(onComplete: () => void) {
        this.type.getAnimator("specialAttack").bind(this.sprite);
        this.sprite.play();
        this.sprite.onComplete = onComplete;
    }

    fillResetPacket(packet: EntityResetPacket) {
        super.fillResetPacket(packet);
        packet.energy = this.energy;
    }

    onReset(packet: EntityResetPacket) {
        super.onReset(packet);
        this.energy = packet.energy;
    }

    static createBody(world: World) {
        let body = world.physics.createBody({
            type: "dynamic",
            fixedRotation: true,
        });

        const width = 2;
        const height = 2;

        body.createFixture({
            shape: planck.Box(
                width / 2, height / 2,
                planck.Vec2(0, height / 2),
                0
            ),
            density: 1,
            // Collide with everything BUT players (or anything that is the same category as the player)
            filterCategoryBits: Player.COLLISION_CATEGORY,
            filterMaskBits: ~Player.COLLISION_CATEGORY,
        });
        //sprite.setFlipX(conf.playerIndex % 2 != 0);
        return body;
    }
}