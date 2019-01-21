import {Behaviour} from "./behaviour";
import * as Actions from "../input/actions"
import {World} from "../world/world";

// @ts-ignore
import * as planck from "planck-js";

export class IdleBehaviour extends Behaviour {
    id = "idle";
    animated = true;

    stillTime = 0;

    initialize() {
        super.initialize();
        this.hook(() => Actions.LEFT.pressed, this.layer.behaviours.get("walk_left")!);
        this.hook(() => Actions.RIGHT.pressed, this.layer.behaviours.get("walk_right")!);
    }

    onEnable() {
        super.onEnable();
        this.stillTime = 0;
    }

    onPrePhysics() {
        super.onPrePhysics();

        const vel = this.player.body.getLinearVelocity();
        vel.x *= 0.9;
        this.player.body.setLinearVelocity(vel);

        this.stillTime += World.TIME_STEP;

        let friction;

        if (!this.player.isTouchingGround) {
            friction = 0;
        } else if (this.stillTime < 0.2) {
            friction = 0.2;
        } else {
            friction = 100;
        }

        this.player.friction = friction;
    }

    onAnimationEnable() {
        super.onAnimationEnable();
        this.player.idle();
    }

    onAnimationDisable() {
        super.onAnimationDisable();
        this.player.sprite.stop();
    }
}

abstract class WalkBehaviour extends Behaviour {
    animated = true;

    maxVelocity = 7;
    abstract horizontalImpulse: number;

    onPrePhysics() {
        super.onPrePhysics();
        const vel = this.player.body.getLinearVelocity();

        // cap max velocity on x
        if (Math.abs(vel.x) > this.maxVelocity) {
            vel.x = Math.sign(vel.x) * this.maxVelocity;
            this.player.body.setLinearVelocity(vel);
        }

        this.player.friction = this.player.isTouchingGround ? 0.2 : 0;
        const isMaxVelReached = this.horizontalImpulse < 0 ? (vel.x < -this.maxVelocity) : (vel.x > this.maxVelocity);

        // apply horizontal impulse, but only if max velocity is not reached yet
        if (!isMaxVelReached) {
            this.player.body.applyLinearImpulse(planck.Vec2(this.horizontalImpulse, 0), this.player.body.getWorldCenter(), true);
        }
    }

    onAnimationEnable() {
        super.onAnimationEnable();
        this.player.type.getAnimator("walk").bind(this.player.sprite);
        this.player.sprite.play();
    }

    onAnimationDisable() {
        super.onAnimationDisable();
        this.player.sprite.stop();
    }
}

export class WalkLeftBehaviour extends WalkBehaviour {
    id = "walk_left";

    horizontalImpulse = -2;

    initialize() {
        super.initialize();
        this.hook(() => !Actions.LEFT.pressed, this.layer.behaviours.get("idle")!)
    }

    onEnable() {
        super.onEnable();
        this.player.flipX = true;
    }
}

export class WalkRightBehaviour extends WalkBehaviour {
    id = "walk_right";

    horizontalImpulse = 2;

    initialize() {
        super.initialize();
        this.hook(() => !Actions.RIGHT.pressed, this.layer.behaviours.get("idle")!)
    }

    onEnable() {
        super.onEnable();
        this.player.flipX = false;
    }
}

