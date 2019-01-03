import {Phase} from "./phase";

export class GamePhase extends Phase {
    name = "game";

    overlay: HTMLDivElement;

    constructor() {
        super();

        this.overlay = document.getElementById("game-overlay") as HTMLDivElement;
    }

    onShow() {
        this.overlay.style.display = "block";
    }

    onMessage(packet: any) {
    }

    onDismiss() {
        this.overlay.style.display = "none"
    }
}
