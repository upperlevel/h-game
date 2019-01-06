import {Behaviour} from "./behaviour";

export class IdleBehaviour extends Behaviour {
    id = "idle";
    animated = true;

    initialize() {
        super.initialize();
        this.hook(() => this.layer.scene.actions.LEFT.isDown, this.layer.behaviours.get("walk_left")!);
        this.hook(() => this.layer.scene.actions.RIGHT.isDown, this.layer.behaviours.get("walk_right")!);
    }

    onEnable() {
        super.onEnable();
        this.player.body.setVelocityX(0.0);
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

    maxVelocity = 200.0;

    initialize() {
        super.initialize();
        this.hook(() => this.layer.scene.actions.LEFT.isUp, this.layer.behaviours.get("idle")!)
    }

    onEnable() {
        super.onEnable();
        this.player.sprite.flipX = true;
        this.player.body.setVelocityX(-this.maxVelocity);
    }

    onAnimationEnable() {
        super.onAnimationEnable();
        this.player.sprite.anims.play(this.player.type.animations["walk"]);
    }

    onAnimationDisable() {
        super.onAnimationDisable();
        this.player.sprite.anims.stop();
    }
}

export class WalkRightBehaviour extends Behaviour {
    id = "walk_right";
    animated = true;

    maxVelocity = 250.0;

    initialize() {
        super.initialize();
        this.hook(() => this.layer.scene.actions.RIGHT.isUp, this.layer.behaviours.get("idle")!)
    }

    onEnable() {
        super.onEnable();
        this.player.sprite.flipX = false;
        this.player.body.setVelocityX(this.maxVelocity);
    }

    onAnimationEnable() {
        super.onAnimationEnable();
        this.player.sprite.anims.play(this.player.type.animations["walk"]);
    }

    onAnimationDisable() {
        super.onAnimationDisable();
        this.player.sprite.anims.stop();
    }
}

