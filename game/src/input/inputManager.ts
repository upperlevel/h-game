import {Key} from "./key";

export class InputManager {
    static pressTime = new Map<string, number>();
    static updateCount = 0;

    static onEnable() {
        window.addEventListener("keydown", this.onKeyDown.bind(this));
        window.addEventListener("keyup", this.onKeyUp.bind(this));
    }

    static onKeyDown(event: KeyboardEvent) {
        console.log(event);
        // Avoid multiple JustPressed when the keydown is called multiple time in a long press
        if (!this.pressTime.has(event.key)) {
            this.pressTime.set(event.key, this.updateCount);
        }
    }

    static onKeyUp(event: KeyboardEvent) {
        console.log(event);
        this.pressTime.delete(event.key)
    }

    static isPressed(key: string): boolean {
        return this.pressTime.has(key);
    }

    static isJustPressed(key: string): boolean {
        return this.pressTime.get(key) == this.updateCount;
    }

    static getKey(value: string): Key {
        return new Key(value);
    }

    static onPostUpdate() {
        this.updateCount++;
    }
}

InputManager.onEnable();

