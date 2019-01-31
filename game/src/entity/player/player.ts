// @ts-ignore
import * as planck from "planck-js"
import * as Actions from "../../input/actions";

import {Entity} from "../entity";
import {createPlayerBehaviour} from "../../behaviour/behaviours";
import {BehaviourManager} from "../../behaviour/behaviour";
import {EntityResetPacket, PlayerEntitySpawnMeta} from "../../protocol";
import {World} from "../../world/world";
import {EntityType} from "../entityType";
import {GamePlayerHud} from "./gamePlayerHud";
import {PlayerHud} from "./playerHud";

export interface PlayerConfig {
    name?: string,
    gameHud?: boolean,
}

export class Player extends Entity {
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

    name: string;

    // override
    damageable = true;
    private behaviour: BehaviourManager;

    huds: PlayerHud[] = [];

    get friction(): number {
        return this.body.getFixtureList()!.getFriction()
    }

    set friction(v: number) {
        this.body.getFixtureList()!.setFriction(v);
    }

    get left(): boolean {
        return this.flipX
    }

    set left(v: boolean) {
        this.flipX = v;
    }

    get mouthX(): number {
        return this.x + 0.2 * (this.left ? -1 : 1);
    }

    get mouthY(): number {
        return this.y + this.height * 0.65;
    }


    constructor(world: World, body: planck.Body, active: boolean, type: EntityType, config: PlayerConfig) {
        super(world, body, active, type);
        this.behaviour = createPlayerBehaviour(this);
        this.name = config.name || "Ulisse";

        if (config.gameHud == null || config.gameHud) {
            this.huds.push(new GamePlayerHud(this));
        }

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

        if (this.y < -10) {
            this.damage(this.life);
        }

        if (this.active && Actions.JUMP.pressed && this.isTouchingGround) {
            this.jump();
        }
        this.energy = Math.min(this.energy + this.energyGainPerSec * deltatime, this.maxEnergy);

        for (const hud of this.huds) {
            hud.update(this);
        }
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
        this.type.getAnimator("idle").play(this.sprite);
    }

    shoutComic(text: string) {
        this.world.createComicPopup({
            x: this.mouthX,
            y: this.mouthY,
            left: this.left,
            fadeIn: 0.3,
            stay: 5,
            text: text,
            style: {
                align: "center",
                fontFamily: "pixeled",
                fill: 0xffffff,
            }
        });
        if (this.active) {
            this.world.sendPacket({
                type: "player_shout",
                entityId: this.id,
                text: text,
            })
        }
    }

    attack(onComplete: () => void) {
        this.type.getAnimator("attack").play(this.sprite);
        this.sprite.onComplete = onComplete;
    }

    canSpecialAttack(): boolean {
        return this.energy >= this.specialAttackEnergy;
    }

    specialAttack(onComplete: () => void) {
        this.type.getAnimator("specialAttack").play(this.sprite);
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

    onDespawn() {
        super.onDespawn();

        for (const hud of this.huds) {
            hud.onDespawn(this);
        }
        this.huds = [];
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
