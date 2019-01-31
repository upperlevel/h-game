import {Popup} from "./popup";
import {World} from "../world/world";
import {PopupSpawnPacket} from "../protocol";
import {Emitter} from "../world/emitter";
import {Entity} from "../entity/entity";

export interface LaserPopupConfig {
    x: number;
    y: number;
    speed: number;
    distance: number;
    scale: number;
}

export class LaserPopup implements Popup {
    static LIFE_TIME = 3;

    world: World;
    shooter: Entity;
    data: LaserPopupConfig;
    emitter: Emitter;

    constructor(world: World, shooter: Entity, data: LaserPopupConfig) {
        this.world = world;
        this.shooter = shooter;

        data.distance = Math.abs(data.distance);
        this.data = data;

        this.emitter = world.createEmitter({
            x: data.x,
            y: data.y,
            scale: data.scale,
            textures: ["assets/game/particle.png"],
            config: PIXI.loader.resources["assets/game/laser.json"].data,
        });

        this.emitter.container.scale.x = this.emitter.container.scale.x * (data.speed / Math.abs(data.speed));
    }

    update(delta: number): boolean {
        if (Math.abs(this.emitter.container.x - this.data.x) >= this.data.distance) {
            this.emitter.remove();
            return true;
        }
        this.emitter.container.x += this.data.speed * delta;
        return false;
    }

    toPacket(): PopupSpawnPacket {
        return {
            type: "spawn_popup",
            popupType: "laser",
            data: {
                shooter: this.shooter.id,
                x: this.data.x,
                y: this.data.y,
                distance: this.data.distance,
                scale: this.data.scale,
            },
        }
    }

    static fromPacket(world: World, packet: PopupSpawnPacket) {
        return new LaserPopup(world, world.entities.get(packet.data.shooter)!, packet.data);
    }
}
