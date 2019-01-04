import {Behaviour, BehaviourLayer} from "./behaviour";
import {hgame} from "../index";

export class AttackBehaviour extends Behaviour {
    id = "attack";
    animated = true;

    onAnimationEnable() {
        this.player.attack(() => this.layer.active = undefined)
    }

    onAnimationDisable() {}
}

export class SpecialAttackBehaviour extends Behaviour {
    id = "special_attack";
    animated = true;

    onAnimationEnable() {
        // TODO
        this.player.specialAttack(() => this.layer.active = undefined)
    }

    onAnimationDisable() {}
}

export class NoAttackBehaviour extends Behaviour {
    id = "none";
    animated = false;

    initialize() {
        super.initialize();
        this.hook(() => hgame.actions!.ATTACK.isDown, this.layer.behaviours.get("attack")!);
        this.hook(() => hgame.actions!.SPECIAL_ATTACK.isDown, this.layer.behaviours.get("special_attack")!);
    }
}


