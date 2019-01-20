import {Behaviour} from "./behaviour";
import * as Action from "../input/actions";

export class AttackBehaviour extends Behaviour {
    id = "attack";
    animated = true;

    onAnimationEnable() {
        this.player.attack(() => this.layer.active = this.layer.behaviours.get("none")!)
    }

    onAnimationDisable() {}
}

export class SpecialAttackBehaviour extends Behaviour {
    id = "special_attack";
    animated = true;

    onAnimationEnable() {
        this.player.specialAttack(() => this.layer.active = this.layer.behaviours.get("none")!)
    }

    onAnimationDisable() {}
}

export class NoAttackBehaviour extends Behaviour {
    id = "none";
    animated = false;

    initialize() {
        super.initialize();
        this.hook(() => Action.ATTACK.justPressed, this.layer.behaviours.get("attack")!);
        this.hook(() => Action.SPECIAL_ATTACK.justPressed && this.player.canSpecialAttack(), this.layer.behaviours.get("special_attack")!);
    }
}


