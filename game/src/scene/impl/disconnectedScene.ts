import {OverlayScene} from "../overlayScene";

import {SceneManager} from "../sceneManager";
import {ConnectingScene} from "./connectingScene";

export class DisconnectedOverlay extends OverlayScene {
    manager: SceneManager;
    connectingScene: ConnectingScene;

    retryButton: HTMLButtonElement;

    constructor(manager: SceneManager, connectingScene: ConnectingScene) {
        super("no-connection-overlay");

        this.manager = manager;
        this.connectingScene = connectingScene;

        this.retryButton = document.getElementById("connection-retry-btn") as HTMLButtonElement;
        this.retryButton.onclick = this.onRetry.bind(this);
    }

    onRetry() {
        this.manager.setScene(this.connectingScene);
    }
}
