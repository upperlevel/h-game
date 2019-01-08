import {SceneWrapper} from "../sceneWrapper";
import {DisconnectedOverlay} from "./disconnectedOverlay";
import {ConnectionConfig} from "./connectionConfig";

export class DisconnectedScene extends SceneWrapper {
    overlay: DisconnectedOverlay;

    config?: ConnectionConfig;

    constructor() {
        super({key: "disconnected"});

        this.overlay = new DisconnectedOverlay(this);
    }

    onInit(config: ConnectionConfig) {
        this.config = config;
    }

    retry() {
        this.scene.start("connecting", this.config!);
    }

    onCreate() {
        this.overlay.show();
    }

    onShutdown() {
        this.overlay.hide();
    }
}
