import {EntityResetPacket, EntitySpawnMeta, EntitySpawnPacket, GamePacket} from "../protocol"
import {GameScene} from "../scene/game/gameScene";
import Sprite = Phaser.Physics.Arcade.Sprite;
import Color = Phaser.Display.Color;

import {Position} from "./util";
import Animation = Phaser.Animations.Animation;
import AnimationFrame = Phaser.Animations.AnimationFrame;
import {Popup} from "./popup";
import Scene = Phaser.Scene;

export abstract class Entity {
    id = -1;
    type: EntityType;

    scene: GameScene;

    sprite: Sprite;

    maxLife = 100;
    life = this.maxLife;
    damageable = false;

    destroyed = false;

    readonly active: boolean;

    constructor(scene: GameScene, sprite: Sprite, active: boolean, type: EntityType) {
        this.scene = scene;
        this.sprite = sprite;
        this.active = active;
        this.type = type;
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
        return this.sprite.width * this.sprite.scaleX;
    }

    get height() {
        return this.sprite.height * this.sprite.scaleY;
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

    onFrameOnce(targetFrame: number, callback: () => void) {
        let key = this.sprite.anims.getCurrentKey();
        this.sprite.once("animationupdate", (animation: Animation, frame: AnimationFrame) => {
            if (animation.key != key) return;

            if (frame.index == targetFrame) {
                callback();
            } else {
                this.onFrameOnce(targetFrame, callback);
            }
        })
    }

    destroy() {
        this.sprite.destroy(true);
    }
}
