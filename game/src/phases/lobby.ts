import {Phase} from "./phase";

export class LobbyPhase implements Phase {
    overlay: HTMLDivElement;

    show() {
        this.overlay = document.getElementById("lobby-overlay") as HTMLDivElement;
        this.overlay.style.display = "block";
    }

    dismiss() {
        this.overlay.style.display = "none"
    }
}
