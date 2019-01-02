import {Phase, Phases} from "../phases/phase";
import {HGame} from "../index";

export class NoConnectionPhase implements Phase {
    overlay: HTMLDivElement;
    retryButton: HTMLButtonElement;

    constructor() {
        this.overlay = document.getElementById("no-connection-overlay") as HTMLDivElement;

        this.retryButton = document.getElementById("connection-retry-btn") as HTMLButtonElement;
        this.retryButton.onclick = () => {
            HGame.instance.phaseManager.show(Phases.CONNECTING)
        }
    }

    show() {
        this.overlay.style.display = "block";
    }

    dismiss() {
        this.overlay.style.display = "none";
    }
}

