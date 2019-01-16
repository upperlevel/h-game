import {Entity} from "./entity";
import {World} from "../world";
import {Animator} from "../util/animator";

export abstract class EntityType {
    readonly id: string;
    readonly asset: string;

    animators = new Map<string, Animator>();

    constructor(id: string, asset: string) {
        this.id = id;
        this.asset = asset;
    }

    addAnimator(animator: Animator) {
        this.animators.set(animator.id, animator);
    }

    getAnimator(id: string): Animator {
        return this.animators.get(id)!;
    }

    onLoad() {
        for (const animator of this.animators.values()) {
            animator.onLoad();
        }
    }

    abstract create(world: World, active: boolean): Entity;
}
