import * as Phaser from "phaser";
import {Entity} from "./entity";
import {Behaviour, BehaviourManager} from "../behaviour/behaviour";
import {hgame} from "../index";

export abstract class Player extends Entity {
    behaviour = BehaviourManager.createPlayerBehaviour(this);

    maxEnergy = 1.0;
    energy = 1.0;
    energyGainPerSecond = 0.05;

    attackPower = 0.1;
    jumpForce = 100;

    name = "Ulisse";

    // override
    damageable = true;

    abstract attackAnimationId: string;
    abstract specialAttackAnimationId: string;
    abstract idleAnimationId: string;
    abstract walkLeftTextureId: string;
    abstract walkRightTextureId: string;


    update(deltatime: number) {
        super.update(deltatime);

        this.behaviour.update();

        if (hgame.actions!.JUMP.isDown && this.sprite.body.onFloor()) {
            this.jump();
        }
        this.energy = Math.min(this.energy + this.energyGainPerSecond * deltatime, this.maxEnergy);
    }

    jump() {
        this.sprite.body.velocity.y += this.jumpForce;
        // TODO: send packet
    }

    attack(callBack: any) {
        this.sprite.anims.play(this.attackAnimationId);
        this.sprite.once("animationcomplete", callBack);
    }

    specialAttack(callBack: any) {
        this.sprite.anims.play(this.specialAttackAnimationId);
        this.sprite.once("animationcomplete", callBack);
    }

    idle() {
        this.sprite.anims.play(this.idleAnimationId)
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

class Santy extends Player {
    attackAnimationId = "santy_attack";
    specialAttackAnimationId = "santy_special_attack";
    idleAnimationId = "santy_idle";
    walkLeftTextureId = "santy_left";
    walkRightTextureId = "walk_right";

    type = "santy";

    sprite: Phaser.Physics.Arcade.Sprite;

    constructor(physics: Phaser.Physics.Arcade) {
        super();
        this.sprite = physics.add.sprite(200, 200, "");
    }
}
