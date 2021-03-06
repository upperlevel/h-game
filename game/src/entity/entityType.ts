import {Entity} from "./entity";
import {World} from "../world/world";
import {Animator} from "../util/animator";

export abstract class EntityType {
    readonly abstract id: string;

    readonly width: number = 2;
    readonly height: number = 2;

    readonly assets: string[] = [];

    readonly animators = new Map<string, Animator>();

    addAsset(asset: string) {
        this.assets.push(asset);
    }

    addAnimator(animator: Animator) {
        this.animators.set(animator.id, animator);
    }

    getAnimator(id: string): Animator {
        return this.animators.get(id)!;
    }

    abstract create(world: World, active?: boolean, config?: any): Entity;
}
