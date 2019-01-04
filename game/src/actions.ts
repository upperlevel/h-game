import * as Phaser from "phaser"

type Key = Phaser.Input.Keyboard.Key;
import Scene = Phaser.Scene;

export class Keyboard {
    JUMP: Key;
    LEFT: Key;
    RIGHT: Key;
    ATTACK: Key;
    SPECIAL_ATTACK: Key;

    constructor(scene: Scene) {
        let keyboard = scene.input.keyboard;
        this.JUMP = keyboard.addKey("W");
        this.LEFT = keyboard.addKey("A");
        this.RIGHT = keyboard.addKey("D");
        this.ATTACK = keyboard.addKey("SPACE");
        this.SPECIAL_ATTACK = keyboard.addKey("J");
    }
}

