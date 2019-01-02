import {Phase} from "./phase";

export class GamePhase implements Phase {
    overlay: HTMLDivElement;

    constructor() {
        this.overlay = document.getElementById("game-overlay") as HTMLDivElement;
    }

    show() {
        this.overlay.style.display = "block";
    }

    dismiss() {
        this.overlay.style.display = "none"
    }
}
