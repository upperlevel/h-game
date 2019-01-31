import {Text} from "../world/text";
import {World} from "../world/world";
import {Terrain} from "../world/terrain";
import {Popup} from "./popup";

export class DamagePopup implements Popup {
    static VELOCITY = 1;
    static VIBRATION_FREQUENCY = 10;
    static VIBRATION_AMPLITUDE = 0.25;
    static MAX_DISTANCE = 2.5;

    text: Text;

    spawnX: number;
    spawnY: number;

    constructor(world: World, data: Terrain.Text) {
        this.text = new Text(world, data);

        this.spawnX = data.x;
        this.spawnY = data.y;
    }

    update(delta: number): boolean {
        this.text.y += DamagePopup.VELOCITY * delta;
        this.text.x = this.spawnX + Math.sin((this.spawnY - this.text.y) * DamagePopup.VIBRATION_FREQUENCY) * DamagePopup.VIBRATION_AMPLITUDE;

        const distance = this.text.y - this.spawnY;
        this.text.sprite.alpha = (DamagePopup.MAX_DISTANCE - distance) / DamagePopup.MAX_DISTANCE;
        if (distance >= DamagePopup.MAX_DISTANCE) {
            this.text.remove();
            return true;
        }


        return false;
    }
}
