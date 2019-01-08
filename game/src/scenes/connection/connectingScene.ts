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
        this.scene.start(this.config!.nextScene);
    }

    onDisconnect() {
        this.scene.start("disconnected", this.config!);
    }

    onCreate() {
        this.overlay.show();

        const connector = this.config!.connector;

        connector.events.once("connect", this.onConnect, this);
        connector.events.once("disconnect", this.onDisconnect, this);

        connector.connect();
    }

    onUpdate() {
    }

    onShutdown() {
        const connector = this.config!.connector;

        connector.events.removeListener("connect", this.onConnect, this, true);
        connector.events.removeListener("disconnect", this.onDisconnect, this, true);

        this.overlay.hide();
    }
}
