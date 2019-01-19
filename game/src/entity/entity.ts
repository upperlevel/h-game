import {EntityResetPacket, EntitySpawnMeta} from "../protocol"

import {Position} from "./util";
import {EntityType} from "./entityType";
import {World} from "../world";
import AnimatedSprite = PIXI.extras.AnimatedSprite;

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

    get isTouchingGround(): boolean {
        return this.groundContactCount > 0
    }

    constructor(world: World, body: planck.Body, active: boolean, type: EntityType) {
        this.world = world;
        this.body = body;
        this.active = active;
        this.type = type;

        this.sprite = new AnimatedSprite(type.getAnimator("idle")!.frames!);

        // A sprite size is supposed to be always 48x48, make that more flexible
        this.sprite.scale.x = type.width / 48;
        this.sprite.scale.y = type.height / 48;

        this.syncPosition();
    }

    /** Synchronizes the sprite position with the body's one. */
    private syncPosition() {
        const position = this.body.getPosition();

        this.sprite.x = position.x;
        this.sprite.y = this.world.height - position.y;
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
        return this.sprite.width * this.sprite.scale.x;
    }

    get height() {
        return this.sprite.height * this.sprite.scale.y;
    }

    get isFacingLeft() {
        return this.sprite.scale.x < -1;
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
            this.sendReset();// Broadcast the position
        }
    }

    damage(amount: number) {
        if (!this.damageable) {
            return;
        }

        this.life -= amount;
        //TODO: this.scene.popup(new Popup(this.scene, this.x, this.y, amount.toFixed(2), "red"));

        if (this.life <= 0) {
            this.respawn();
            return;
        }

        // Only done if the player isn't death.
        this.sprite.tint = 0xff0000;
        setTimeout(() => this.sprite.tint = 0xffffff, 250);
    }

    // TODO: throw

    createSpawnMeta(): EntitySpawnMeta | undefined {
        return undefined;
    }

    loadSpawnMeta(meta: EntitySpawnMeta) {
    }

    fillResetPacket(packet: EntityResetPacket) {
        packet.x = this.x;
        packet.y = this.y;
        packet.life = this.life;
    }

    onReset(packet: EntityResetPacket) {
        this.x = packet.x;
        this.y = packet.y;
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

    destroy() {
        this.sprite.destroy(true);
    }
}
