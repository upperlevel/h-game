import {Behaviour} from "./behaviour";
import {hgame} from "../index";

export class IdleBehaviour extends Behaviour {
    id = "idle";
    animated = false;

    initialize() {
        super.initialize();
        this.hook(() => hgame.actions!.LEFT.isDown, this.layer.behaviours.get("walk_left")!);
        this.hook(() => hgame.actions!.RIGHT.isDown, this.layer.behaviours.get("walk_right")!);
    }

    onAnimationEnable() {
        super.onAnimationEnable();
        this.player.idle();
    }

    onAnimationDisable() {
        super.onAnimationDisable();
        this.player.sprite.anims.stop();
    }
}

export class WalkLeftBehaviour extends Behaviour {
    id = "walk_left";
    animated = true;

    maxVelocity = 7.0;

    initialize() {
        super.initialize();
        this.hook(() => hgame.actions!.LEFT.isUp, this.layer.behaviours.get("idle")!)
    }

    onEnable() {
        super.onEnable();
        this.player.sprite.flipX = true;
        this.player.sprite.setVelocityX(-this.maxVelocity);
    }

    onAnimationEnable() {
        super.onAnimationEnable();
        this.player.sprite.anims.play(this.player.walkLeftTextureId);
    }

    onAnimationDisable() {
        super.onAnimationDisable();
        this.player.sprite.anims.stop();
    }
}

export class WalkRightBehaviour extends Behaviour {
    id = "walk_right";
    animated = true;

    maxVelocity = 7.0;

    initialize() {
        super.initialize();
        this.hook(() => hgame.actions!.RIGHT.isUp, this.layer.behaviours.get("idle")!)
    }

    onEnable() {
        super.onEnable();
        this.player.sprite.flipX = false;
        this.player.sprite.setVelocityX(this.maxVelocity);
    }

    onAnimationEnable() {
        super.onAnimationEnable();
        this.player.sprite.anims.play(this.player.walkRightTextureId);
    }

    onAnimationDisable() {
        super.onAnimationDisable();
        this.player.sprite.anims.stop();
    }
}

