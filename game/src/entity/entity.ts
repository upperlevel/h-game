import {EntityResetPacket, EntitySpawnMeta, EntitySpawnPacket, GamePacket} from "../protocol"
import {GameScene} from "../scene/game/gameScene";

import {Position} from "./util";
import {Popup} from "./popup";
import {EntityType} from "./entityType";

import AnimatedSprite = PIXI.extras.AnimatedSprite;

export abstract class Entity {
    id = -1;
    type: EntityType;

    scene: GameScene;

    sprite: AnimatedSprite;

    maxLife = 100;
    life = this.maxLife;
    damageable = false;

    destroyed = false;

    readonly active: boolean;

    constructor(scene: GameScene, active: boolean, type: EntityType) {
        this.scene = scene;
        this.active = active;
        this.type = type;

        const spritesheet = PIXI.loader.resources[type.spritesheetPath].spritesheet!;
        this.sprite = new AnimatedSprite(spritesheet.animations["idle"]);
    }

    get x() {
        return this.sprite.x;
    }

    set x(x) {
        this.sprite.x = x;
    }

    get y() {
        return this.sprite.y;
    }

    set y(y) {
        this.sprite.y = y;
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
        return this.sprite.flipX;
    }

    update(delta: number) {
    }

    get body(): Phaser.Physics.Arcade.Body {
        if (this.sprite == null) throw new Error("sprite is null");
        if (this.sprite.body == null) throw new Error("body is null");
        return this.sprite.body as Phaser.Physics.Arcade.Body;
    }

    respawn() {
        this.life = this.maxLife;

        if (this.active) {
            this.position = this.scene.getSpawnLocation();
            this.sendReset();// Broadcast the position
        }
    }

    damage(amount: number) {
        if (!this.damageable) {
            return;
        }

        this.life -= amount;
        this.scene.popup(new Popup(this.scene, this.x, this.y, amount.toFixed(2), "red"));

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
        this.scene.sendPacket(this.createResetPacket())
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

    destroy() {
        this.sprite.destroy(true);
    }
}
