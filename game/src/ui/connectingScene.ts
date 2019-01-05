import {SceneWrapper} from "./sceneWrapper"

import {Overlay} from "./overlay";

import {hgame} from "../index";

class ConnectingOverlay extends Overlay {
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

export class ConnectingScene extends SceneWrapper {
    overlay: ConnectingOverlay;

    constructor() {
        super("connecting");

        this.overlay = new ConnectingOverlay();
    }

    onPreload() {
    }

    onCreate() {
        hgame.reconnect();

        this.overlay.show();

        hgame.getChannel().onopen  = () => this.changeScene("login");
    }

    onUpdate() {
    }

    onShutdown() {
        this.overlay.hide();
    }
}
