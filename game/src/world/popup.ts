import {Text} from "./text";
import {World} from "./world";
import {Terrain} from "./terrain";

export class Popup extends Text {
    static VELOCITY = 1;
    static VIBRATION_FREQUENCY = 10;
    static VIBRATION_AMPLITUDE = 0.25;
    static MAX_DISTANCE = 2.5;

    spawnX: number;
    spawnY: number;

    constructor(world: World, data: Terrain.Text) {
        super(world, data);

        this.spawnX = data.x;
        this.spawnY = data.y;
    }

    update(delta: number): boolean {
        this.y += Popup.VELOCITY * delta;
        this.x = this.spawnX + Math.sin((this.spawnY - this.y) * Popup.VIBRATION_FREQUENCY) * Popup.VIBRATION_AMPLITUDE;

        const distance = this.y - this.spawnY;
        this.sprite.alpha = (Popup.MAX_DISTANCE - distance) / Popup.MAX_DISTANCE;
        if (distance >= Popup.MAX_DISTANCE) {
            this.remove();
            return true;
        }


        return false;
    }
}
