import {DisconnectedScene} from "./disconnectedScene";
import {Overlay} from "../overlay";

export class DisconnectedOverlay extends Overlay {
    scene: DisconnectedScene;

    retryButton: HTMLButtonElement;

    constructor(scene: DisconnectedScene) {
        super("no-connection-overlay");

        this.scene = scene;

        this.retryButton = document.getElementById("connection-retry-btn") as HTMLButtonElement;
        this.retryButton.onclick = scene.retry.bind(scene);
    }
}
