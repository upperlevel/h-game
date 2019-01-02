import {Phase} from "./phase";

export class GamePhase implements Phase {
    overlay: HTMLDivElement;

    show() {
        this.overlay = document.getElementById("game-overlay") as HTMLDivElement;
        this.overlay.style.display = "block";
    }

    dismiss() {
        this.overlay.style.display = "none"
    }
}
