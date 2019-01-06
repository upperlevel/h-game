import {Entity, EntityType} from "./entity";
import {createPlayerBehaviour} from "../behaviour/behaviours";
import {EntityTypes} from "./entities";
import {BehaviourManager} from "../behaviour/behaviour";
import {GameScene} from "../scenes/gameScene";
import Scene = Phaser.Scene;
import Sprite = Phaser.GameObjects.Sprite;
import {EntitySpawnMeta, PlayerEntitySpawnMeta} from "../protocol";

export abstract class Player extends Entity {
    maxEnergy = 1.0;
    energy = 1.0;
    energyGainPerSecond = 0.05;

    attackPower = 0.1;
    jumpForce = 300;

    name = "Ulisse";

    // override
    damageable = true;
    private behaviour: BehaviourManager;

    protected constructor(scene: GameScene, sprite: Sprite, active: boolean, type: EntityType) {
        super(scene, sprite, active, type);
        this.behaviour = createPlayerBehaviour(scene, this);
    }


    update(deltatime: number) {
        super.update(deltatime);

        this.behaviour.update();

        if (this.scene.actions.JUMP.isDown && this.body.onFloor()) {
            this.jump();
        }
        this.energy = Math.min(this.energy + this.energyGainPerSecond * deltatime, this.maxEnergy);
    }

    createSpawnMeta(): EntitySpawnMeta | undefined {
        const meta: PlayerEntitySpawnMeta = {
            type: "player",
            name: this.name,
        };
        return meta;
    }

    loadSpawnMeta(meta: PlayerEntitySpawnMeta) {
        if (meta.type != "player") {
            console.log("Error: invalid player meta type");
            return;
        }
        this.name = meta.name;
    }

    jump() {
        console.log("Jumping");
        this.body.setVelocityY(-this.jumpForce);
        this.scene.sendPacket({
            type: "player_jump",
            entityId: this.id,
        })
    }

    attack(callBack: any) {
        this.sprite.anims.play(this.type.animations["attack"]);
        this.sprite.once("animationcomplete", callBack);
    }

    specialAttack(callBack: any) {
        this.sprite.anims.play(this.type.animations["special_attack"]);
        this.sprite.once("animationcomplete", callBack);
    }

    idle() {
        this.sprite.anims.play(this.type.animations["idle"])
    }

    onAttacked(player: Player) {
        this.life -= player.attackPower;
        if (this.life < 0) {
            // TODO: die
        }
    }

    renderHud() {
        // TODO?
    }
}

export class Santy extends Player {
    constructor(scene: GameScene, active: boolean) {
        super(scene, Santy.createSprite(scene), active, EntityTypes.SANTY);
    }

    static createSprite(scene: Scene): Sprite {
        let sprite = scene.physics.add.sprite(200, 200, "santy").setScale(4);
        sprite.setCollideWorldBounds(true);
        return sprite;
    }

}
