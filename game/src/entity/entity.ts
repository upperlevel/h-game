import {EntityResetPacket, EntitySpawnMeta, EntitySpawnPacket, GamePacket} from "../protocol"
import {GameScene} from "../scenes/game/gameScene";
import Sprite = Phaser.Physics.Arcade.Sprite;
import Color = Phaser.Display.Color;

import {Position} from "./util";
import Animation = Phaser.Animations.Animation;
import AnimationFrame = Phaser.Animations.AnimationFrame;

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

        this.position = this.scene.getSpawnLocation();
    }

    damage(amount: number) {
        if (!this.damageable) {
            return;
        }
        this.life -= amount;

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

export class EntityType {
    id: string;

    preloader: (scene: GameScene) => void;
    loader: (scene: GameScene) => void;

    animations: { [key: string]: string };


    creator: (scene: GameScene, active: boolean) => Entity;

    constructor(
        id: string,
        preloader: (scene: GameScene) => void,
        loader: (scene: GameScene) => void,
        animations: { [key: string]: string },
        creator: (scene: GameScene, active: boolean) => Entity
    ) {
        this.id = id;
        this.preloader = preloader;
        this.loader = loader;
        this.animations = animations;
        this.creator = creator;
    }

    create(scene: GameScene, active: boolean = true): Entity {
        return this.creator(scene, active);
    }
}



