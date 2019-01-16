import {Player} from "../entity/player";

export type Trigger = () => boolean

export abstract class Behaviour {
    layer: BehaviourLayer;
    abstract id: string;
    instantHookCheck = true;

    hooks = new Map<Trigger, Behaviour>([]);
    abstract animated: boolean;

    constructor(layer: BehaviourLayer) {
        this.layer = layer;
    }

    get player(): Player {
        return this.layer.parent!.player;
    }

    hook(trigger: Trigger, behaviour: Behaviour): this {
        this.hooks.set(trigger, behaviour);
        return this;
    }

    resolveHooks(): Behaviour | undefined {
        for (let [trigger, behaviour] of this.hooks) {
            if (trigger()) {
                return behaviour;
            }
        }
        return undefined;
    }

    initialize() {}

    onEnable() {}

    onUpdate() {
        // Each time checks if there is a hook verified.
        if (this.layer.parent == null || !this.layer.parent.active) return;

        let nextHook = this.resolveHooks();
        if (nextHook != null) {
            this.layer.active = nextHook;
        }
    }

    onDisable() {}

    onAnimationEnable() {}

    onAnimationDisable() {}
}

export class BehaviourLayer {
    behaviours = new Map<string, Behaviour>();

    index = 0;
    parent: BehaviourManager | undefined;

    private _active?: Behaviour;

    initialized = false;

    set active(value: Behaviour) {
        let previous = this._active;

        if (this._active != null) {
            //console.log("Disabling ", this._active.id);
            this._active.onDisable()
        }

        let active = this.parent!.active;
        let val: Behaviour | undefined = value;

        do {
            this._active = val;
            if (val != null) {
                //console.log("Resolving: ", val.id);
                // If the behaviour system is active try to resolve the hooks
                // Otherwise just assign null to newVal then go on
                if (active && val.instantHookCheck) {
                    val = val.resolveHooks()
                } else {
                    val = undefined;
                }
            }
        } while (val != null);

        // Now we have the new State, so we can enable and send the change.
        if (this._active != null) {
            //console.log("Enabling ", this._active.id);
            this._active.onEnable();
        }
        this.parent!.onBehaviourChange(this, previous, this._active);
    }

    get active(): Behaviour {
        if (!this.initialized) throw new Error("Not initialized yet");
        return this._active!;
    }

    setActive(behaviourId: string) {
        if (!this.behaviours.has(behaviourId)) {
            throw new Error("BehaviourId not found: " + behaviourId);
        }
        this._active = this.behaviours.get(behaviourId);
    }

    get player(): Player {
        return this.parent!.player;
    }

    register(behaviour: Behaviour): this {
        if (this.initialized) {
            throw new Error("Cannot register behaviour once initialized")
        }
        if (this.behaviours.has(behaviour.id)) {
            throw new Error("Behaviour conflict, two ids registered with the same behaviour");
        }
        this.behaviours.set(behaviour.id, behaviour);

        return this;
    }

    initialize(startBehaviour: string) {
        for (let [name, behaviour] of this.behaviours) {
            behaviour.initialize();
        }

        this.setActive(startBehaviour);
        this.initialized = true;
    }

    update() {
        this.active!.onUpdate();
    }
}

/**
 * This class manages various BehaviourGraphs organized as layers
 */
export class BehaviourManager {
    layers: BehaviourLayer[];
    player: Player;

    private _currentAnimated?: Behaviour;

    /**
     * Is true only when the current behaviour system is active (so it needs to check hooks and send states)
     * If it's passive it doesn't need to check for behaviour changes as they will be sent from the other endpoint
     */
    get active(): boolean {
        return this.player.active;
    }


    constructor(layers: BehaviourLayer[], player: Player) {
        this.layers = layers;
        this.player = player;

        this.layers.forEach((layer, index) => {
            layer.parent = this;
            layer.index = index;
        });
        this.currentAnimated = this.searchAnimation();
    }

    get currentAnimated(): Behaviour | undefined {
        return this._currentAnimated;
    }

    set currentAnimated(value: Behaviour | undefined) {
        if (this._currentAnimated != null) {
            //console.log("Disabling animation: ", this._currentAnimated.id);
            this._currentAnimated.onAnimationDisable();
        }
        this._currentAnimated = value;
        if (value != null) {
            //console.log("Enabling animation: ", value.id);
            value.onAnimationEnable();
        }
    }

    update() {
        for (let layer of this.layers) {
            layer.update();
        }
    }

    searchAnimation(startLayer: number = -1): Behaviour | undefined {
        if (startLayer == -1) {
            startLayer = this.layers.length - 1;
        }
        let currentLayer = startLayer;
        do {
            let behaviour = this.layers[currentLayer].active;
            if (behaviour != null && behaviour.animated) {
                return behaviour;
            }
        } while (--currentLayer >= 0);

        return undefined;
    }

    onBehaviourChange(layer: BehaviourLayer, previous: Behaviour | undefined, next: Behaviour | undefined) {
        let current = this.currentAnimated;

        //console.log("Behaviour change: layer ", layer.index, " prev: ", previous, "next: ", next);
        if (this.active) {
            this.player.world.sendPacket({
                type: "behaviour_change",
                actorId: this.player.id,
                layerIndex: layer.index,
                behaviour: layer.active.id
            });
        }

        if (current == null) {
            // The current one is null
            // use the new one only if animated, else keep the null animation
            if (next != null && next.animated) this.currentAnimated = next;
        } else if (current == previous) {
            // Substitution in the same layer
            // if the next one is animated then choose it
            // else search an animation in the lower layers
            if (next != null && next.animated) {
                this.currentAnimated = next;
            } else {
                this.currentAnimated = this.searchAnimation(current.layer.index - 1);
            }
        } else if (next != null && next.animated && current.layer.index <= next.layer.index) {
            // the next is at an higher layer than the current one (and it is animated)
            this.currentAnimated = next;
        }
    }
}