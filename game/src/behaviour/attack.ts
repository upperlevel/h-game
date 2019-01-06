import {Behaviour} from "./behaviour";

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
        this.hook(() => this.layer.scene.actions.ATTACK.isDown, this.layer.behaviours.get("attack")!);
        this.hook(() => this.layer.scene.actions.SPECIAL_ATTACK.isDown, this.layer.behaviours.get("special_attack")!);
    }
}


