import {EntityResetPacket, EntitySpawnMeta} from "../protocol"

import {Position} from "./util";
import {EntityType} from "./entityType";
import {World} from "../world/world";
import AnimatedSprite = PIXI.extras.AnimatedSprite;
import {DamagePopup} from "../popups/popup";
// @ts-ignore
import * as planck from "planck-js";

import Vec2 = planck.Vec2;

export abstract class Entity {
    id = -1;
    type: EntityType;

    world: World;
    body: planck.Body;

    sprite: AnimatedSprite;

    maxLife = 100;
    life = this.maxLife;
    damageable = false;

    destroyed = false;

    readonly active: boolean;

    private groundContactCount = 0;

    get flipX(): boolean {
        return this.sprite.scale.x < 0;
    }

    set flipX(flipped: boolean) {
        this.sprite.scale.x = Math.abs(this.sprite.scale.x) * (flipped ? -1 : 1);
    }

    get isTouchingGround(): boolean {
        return this.groundContactCount > 0 && this.body.getLinearVelocity().y == 0
    }

    constructor(world: World, body: planck.Body, active: boolean, type: EntityType, startAnimName: string = "idle") {
        this.world = world;
        this.body = body;
        this.active = active;
        this.type = type;

        const animator = type.getAnimator(startAnimName);
        this.sprite = new AnimatedSprite(animator.generateFrames());
        animator.play(this.sprite);

        this.sprite.anchor.x = 0.5;
        this.sprite.anchor.y = 1;

        // A sprite size is supposed to be always 48x48, make that more flexible
        this.sprite.scale.x = type.width / this.sprite.width;
        this.sprite.scale.y = type.height / this.sprite.height;

        this.syncPosition();
    }

    /** Synchronizes the sprite position with the body's one. */
    private syncPosition() {
        const position = this.body.getPosition();

        this.sprite.x = position.x;
        this.sprite.y = this.world.height - position.y
    }

    getPosition(): planck.Vec2 {
        return this.body.getPosition();
    }

    get x() {
        return this.getPosition().x;
    }

    set x(x) {
        let pos = this.getPosition();
        pos.x = x;
        this.body.setPosition(pos);

        this.syncPosition();
    }

    get y() {
        return this.getPosition().y;
    }

    set y(y) {
        let pos = this.getPosition();
        pos.y = y;
        this.body.setPosition(pos);

        this.syncPosition();
    }

    get position(): Position {
        return {x: this.x, y: this.y};
    }

    set position(position: Position) {
        this.x = position.x;
        this.y = position.y;
    }

    get width() {
        return this.type.width;
    }

    get height() {
        return this.type.height;
    }

    setType(type: EntityType) {
        // TODO
    }

    onPrePhysics(timeDelta: number) {
    }

    onUpdate(delta: number) {
        this.syncPosition();
    }

    respawn() {
        this.life = this.maxLife;

        if (this.active) {
            this.position = this.world.getSpawnLocation();
            this.body.setLinearVelocity(planck.Vec2(0, 0));
            this.sendReset();// Broadcast the position
        }
    }

    damage(amount: number) {
        if (!this.damageable) {
            return;
        }

        this.life -= amount;
        this.world.createDamagePopup({
            x: this.x,
            y: this.y + this.height / 2,
            text: amount.toFixed(2),
            isCentered: true,
            height: 0.25,
            style: {
                fontFamily: "pixeled",
                fill: 0xff0000
            }
        });

        if (this.life <= 0) {
            this.respawn();
            return;
        }

        // Only done if the player isn't death.
        this.sprite.tint = 0xff0000;
        setTimeout(() => this.sprite.tint = 0xffffff, 250);
    }

    createSpawnMeta(): EntitySpawnMeta | undefined {
        return undefined;
    }

    loadSpawnMeta(meta: EntitySpawnMeta) {
    }

    fillResetPacket(packet: EntityResetPacket) {
        packet.x = this.x;
        packet.y = this.y;
        let vel = this.body.getLinearVelocity();
        packet.velX = vel.x;
        packet.velY = vel.y;
        packet.life = this.life;
    }

    onReset(packet: EntityResetPacket) {
        this.x = packet.x;
        this.y = packet.y;
        this.body.setLinearVelocity(planck.Vec2(packet.velX, packet.velY));
        this.life = packet.life;
    }

    sendReset() {
        this.world.sendPacket(this.createResetPacket())
    }

    private createResetPacket() {
        let resetPacket: EntityResetPacket = {
            type: "entity_reset",
            entityId: this.id
        };
        this.fillResetPacket(resetPacket);
        this.onReset(resetPacket);
        return resetPacket;
    }

    addSensor(fixture: planck.Fixture) {
        fixture.setUserData({
            onTouchBegin: (other: planck.Fixture) => {
                if (other.getUserData() == "ground") this.groundContactCount++;
            },
            onTouchEnd: (other: planck.Fixture) => {
                if (other.getUserData() == "ground") this.groundContactCount--;
            },
        });
    }

    onDespawn() {
        this.sprite.parent.removeChild(this.sprite);
        this.world.removeBody(this.body);
    }

    onFrameOnce(frame: number, callback: () => void) {
        this.sprite.onFrameChange = (fr) => {
            if (fr != frame) return;
            this.sprite.onFrameChange = () => {};
            callback()
        }
    }

    applyImpulse(powerX: number, powerY: number, centerX: number, centerY: number, sendUpdate: boolean=false) {
        this.body.applyLinearImpulse(planck.Vec2(powerX, powerY), planck.Vec2(centerX, centerY));
        if (sendUpdate) {
            this.world.sendPacket({
                type: "entity_impulse",
                entityId: this.id,
                powerX: powerX,
                powerY: powerY,
                pointX: centerX,
                pointY: centerY,
            });
        }
    }
}
