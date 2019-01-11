import {SceneWrapper} from "../sceneWrapper"

import {ConnectingOverlay} from "./connectingOverlay";
import {ConnectionConfig} from "./connectionConfig";

export class ConnectingScene extends SceneWrapper {
    overlay: ConnectingOverlay;

    config?: ConnectionConfig;

    constructor() {
        super({key: "connecting", active: false});

        this.overlay = new ConnectingOverlay();
    }

    onInit(config: any) {
        this.config = config;
    }

    onPreload() {
    }

    onConnect() {
        this.scene.start(this.config!.nextScene, this.config!.nextSceneParams);
    }

    onDisconnect() {
        this.scene.start("disconnected", this.config!);
    }

    onCreate() {
        this.overlay.show();

        const connector = this.config!.connector;

        connector.subscribe("connect", this.onConnect, this, true);
        connector.subscribe("disconnect", this.onDisconnect, this, true);

        connector.connect();
    }

    onUpdate() {
    }

    onShutdown() {
        const connector = this.config!.connector;

        connector.unsubscribe("connect", this.onConnect, this, true);
        connector.unsubscribe("disconnect", this.onDisconnect, this, true);

        this.overlay.hide();
    }
}
