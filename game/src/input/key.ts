import {InputManager} from "./inputManager";

export class Key {
    value: string;

    constructor(value: string) {
        this.value = value;
    }

    get pressed(): boolean {
        return InputManager.isPressed(this.value);
    }

    get justPressed(): boolean {
        return InputManager.isJustPressed(this.value);
    }

    forceDown() {
        InputManager.onKeyDown({
            key: this.value,
        } as KeyboardEvent);
    }

    forceUp() {
        InputManager.onKeyUp({
            key: this.value,
        } as KeyboardEvent);
    }
}
