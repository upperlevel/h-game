import * as Phaser from "phaser"
import Text = Phaser.GameObjects.Text;

import {GameScene} from "../scenes/game/gameScene";

export class Popup {
    static VELOCITY = 70;
    static VIBRATION = 10;
    static MAX_DISTANCE = 120;

    spawnY: number;
    spawnX: number;
    handle: Text;

    constructor(scene: GameScene, x: number, y: number, text: string, color: string) {
        this.spawnY = y;
        this.spawnX = x;
        this.handle = scene.add.text(x, y, text, {
            fontSize: 16,
            fontFamily: "pixeled",
            color: color,
        }).setOrigin(0.5);
    }

    /**
     * Makes the popup go up and destroys it if reaches the max distance.
     * Returns true if destroyed.
     */
    update(delta: number): boolean {
        this.handle.y -= Popup.VELOCITY * delta / 1000;
        this.handle.x = this.spawnX + Math.sin((this.spawnY - this.handle.y) * 0.10) * Popup.VIBRATION;

        if (this.spawnY - this.handle.y >= Popup.MAX_DISTANCE) {
            this.destroy();
            return true;
        }

        return false;
    }

    destroy() {
        this.handle.destroy();
    }
}

