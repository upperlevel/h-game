import * as particles from "pixi-particles"
import {World} from "./world";
import {Terrain} from "./terrain";

export class Emitter {
    world: World;

    container: PIXI.Container;
    emitter: particles.Emitter;

    constructor(world: World, data: Terrain.Emitter) {
        if (!data.textures) {
            throw "An Emitter must have at least one texture";
        }

        this.world = world;

        // Searches given paths in textures' list
        const textures = data.textures.map(path => PIXI.loader.resources[path].texture);

        this.container = new PIXI.Container();
        this.container.x = data.x;
        this.container.y = data.y;

        // Scales the container based on the first textures' sizes
        const scale = this.container.scale;
        scale.x = scale.y = data.scale / textures[0].width;

        this.emitter = new particles.Emitter(this.container, textures, data.config);
    }

    update(delta: number) {
        this.emitter.update(delta);
    }

    remove() {
        this.world.app.stage.removeChild(this.container);
    }
}
