
export class Key {
    value: string;

    isPressed = false;
    isJustPressed = false;

    constructor(value: string) {
        this.value = value;
    }

    subscribe() {
        window.addEventListener("keyup", this.onUp.bind(this));
        window.addEventListener("keydown", this.onDown.bind(this));
    }

    private onUp(event: any) {
        if (event.key === this.value) {
            if (this.isPressed) {
                this.isPressed = false;
                return;
            }
            event.preventDefault();
        }
    }

    private onDown(event: any) {
        if (event.key === this.value) {
            if (!this.isPressed) {
                this.isPressed = true;
                return;
            }
            event.preventDefault();
        }
    }

    unsubscribe() {
        window.removeEventListener("keyup", this.onUp.bind(this));
        window.removeEventListener("keydown", this.onDown.bind(this));
    }
}
