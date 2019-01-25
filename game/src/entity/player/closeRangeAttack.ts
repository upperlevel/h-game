import {Player} from "./player";
// @ts-ignore
import * as planck from "planck-js"
import Fixture = planck.Fixture;
import FixtureDef = planck.FixtureDef;


class Sensor {
    // a lazy deletion list should be more performant... who cares
    colliders: Player[] = [];

    onTouchBegin(other: Fixture) {
        const entity = other.getUserData();

        if (entity == null) return;

        this.colliders.push(entity);
    }

    onTouchEnd(other: Fixture) {
        const entity = other.getUserData();

        if (entity == null) return;

        const index = this.colliders.indexOf(entity);

        if (index < 0) return;

        this.colliders.splice(index, 1);
    }
}

export class CloseRangeAttack {
    owner: Player;
    leftSensor = new Sensor();
    rightSensor = new Sensor();

    constructor(player: Player) {
        this.owner = player;

        const w = Player.WIDTH / 4;
        const h = Player.HEIGHT / 2;

        const leftSensorDef: FixtureDef = {
            shape: planck.Box(w, h, planck.Vec2(-w, h), 0),
            isSensor: true,
            userData: this.leftSensor,
            // Collide ONLY with players
            filterMaskBits: Player.COLLISION_CATEGORY,
        };

        const rightSensorDef: FixtureDef = {
            shape: planck.Box(w, h, planck.Vec2(w, h), 0),
            isSensor: true,
            userData: this.rightSensor,
            // Collide ONLY with players
            filterMaskBits: Player.COLLISION_CATEGORY,
        };

        player.body.createFixture(leftSensorDef);
        player.body.createFixture(rightSensorDef);
    }

    getContacts(): Player[] {
        return this.owner.flipX ? this.leftSensor.colliders : this.rightSensor.colliders;
    }
}
