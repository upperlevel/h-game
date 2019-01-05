import {SceneWrapper} from "./sceneWrapper";

import {Overlay} from "./overlay";

import {hgame} from "../index";

class DisconnectedOverlay extends Overlay {
    scene: DisconnectedScene;

    retryButton: HTMLButtonElement;

    constructor(scene: DisconnectedScene) {
        super("no-connection-overlay");

        this.scene = scene;

        this.retryButton = document.getElementById("connection-retry-btn") as HTMLButtonElement;
        this.retryButton.onclick = () => {
            this.scene.changeScene("connecting")
        }
    }
}

export class DisconnectedScene extends SceneWrapper {
    overlay?: DisconnectedOverlay;

    constructor() {
        super("disconnected");
    }

    onPreload() {
        this.overlay = new DisconnectedOverlay(this);
    }

    onCreate() {
        this.overlay!.show();
    }

    onUpdate() {
    }

    onShutdown() {
        this.overlay!.hide();
    }
}
