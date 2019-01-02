import {Phase} from "./phase";

export class LobbyPhase implements Phase {
    overlay: HTMLDivElement;

    constructor() {
        this.overlay = document.getElementById("lobby-overlay") as HTMLDivElement;
    }

    show() {
        this.overlay.style.display = "block";
    }

    dismiss() {
        this.overlay.style.display = "none"
    }
}
