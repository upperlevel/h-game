import {Phase, Phases} from "./phase";
import {HGame} from "../index";

export class ConnectingPhase implements Phase {
    overlay: HTMLDivElement;
    label: HTMLDivElement;

    labelTimer: number = -1;

    constructor() {
        this.overlay = document.getElementById("connecting-overlay") as HTMLDivElement;
        this.label = document.getElementById("connecting-label") as HTMLDivElement;
    }

    show() {
        this.overlay.style.display = "block";

        HGame.instance.reconnect();

        let timer = 0;
        this.labelTimer = setInterval(() => {
            this.label.innerText = "Connecting" + "...".substring(2 - timer % 3);
            timer++;

        }, 500)
    }

    dismiss() {
        clearInterval(this.labelTimer);
        this.overlay.style.display = "none";
    }
}

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
