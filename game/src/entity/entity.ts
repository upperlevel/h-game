import {EntityResetPacket, EntitySpawnMeta, EntitySpawnPacket, GamePacket} from "../protocol"
import {GameScene} from "../scenes/game/gameScene";
import Sprite = Phaser.GameObjects.Sprite;
import Color = Phaser.Display.Color;

import {Position} from "./util";

export abstract class Entity {
    id = -1;
    type: EntityType;

    scene: GameScene;

    sprite: Sprite;

    maxLife = 1.0;
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
        return {x: this.x, y: this.y} as Position;
    }

    set position(position: Position) {
        this.x = position.x;
        this.y = position.y;
    }

    get isFacingLeft() {
        return this.sprite.flipX;
    }

    update(deltatime: number) {
    }

    get body(): Phaser.Physics.Arcade.Body {
        if (this.sprite == null) throw new Error("sprite is null");
        if (this.sprite.body == null) throw new Error("body is null");
        return this.sprite.body as Phaser.Physics.Arcade.Body;
    }

    respawn() {
        this.life = this.maxLife;

        this.position = this.scene.spawn;
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
        packet.x = this.body.x;
        packet.y = this.body.y;
    }

    onReset(packet: EntityResetPacket) {
        // TODO: de-comment this only after the physics system doesn't depend from the camera size
        //this.sprite.setPosition(packet.x, packet.y);
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



