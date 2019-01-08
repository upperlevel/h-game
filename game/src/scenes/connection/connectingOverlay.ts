import {Overlay} from "../overlay";

export class ConnectingOverlay extends Overlay {
    label: HTMLDivElement;
    labelTimer: number = -1;

    constructor() {
        super("connecting-overlay");
        this.label = document.getElementById("connecting-label") as HTMLDivElement;
    }

    onShow() {
        let timer = 0;
        this.labelTimer = setInterval(() => {
            this.label!.innerText = "Connecting" + "...".substring(2 - timer % 3);
            timer++;
        }, 500);
    }

    onHide() {
        clearInterval(this.labelTimer);
    }
}
