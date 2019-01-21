import {Player} from "../entity/player";
import * as move from "./move";
import * as attack from "./attack";
import {BehaviourLayer, BehaviourManager} from "./behaviour";
import {GameScene} from "../scene/game/gameScene";
import {World} from "../world/world";

export function createPlayerBehaviour(entity: Player): BehaviourManager {

    let moveLayer = new BehaviourLayer();
    moveLayer.register(new move.IdleBehaviour(moveLayer));
    moveLayer.register(new move.WalkLeftBehaviour(moveLayer));
    moveLayer.register(new move.WalkRightBehaviour(moveLayer));
    moveLayer.initialize("idle");

    let attackLayer = new BehaviourLayer();
    attackLayer.register(new attack.NoAttackBehaviour(attackLayer));
    attackLayer.register(new attack.AttackBehaviour(attackLayer));
    attackLayer.register(new attack.SpecialAttackBehaviour(attackLayer));
    attackLayer.initialize("none");

    return new BehaviourManager([moveLayer, attackLayer], entity);
}
