import {OverlayScene} from "./overlayScene";

import {Connector} from "../connector/connector";
import {SceneManager} from "./sceneManager";
import {Scene} from "./scene"

import {DisconnectedScene} from "./disconnectedScene";

export class ConnectingScene extends OverlayScene {
    manager: SceneManager;
    connector: Connector;
    next?: Scene;

    label: HTMLDivElement;
    labelTimer = -1;

    constructor(manager: SceneManager, connector: Connector, next?: Scene) {
        super("connecting-overlay");

        this.manager = manager;
        this.connector = connector;
        this.next = next;

        this.label = document.getElementById("connecting-label") as HTMLDivElement;
    }

    onConnect() {
        this.manager.setScene(this.next);
    }

    onDisconnect() {
        this.manager.setScene(new DisconnectedScene(this));
    }

    onEnable() {
        let timer = 0;
        this.labelTimer = setInterval(() => {
            this.label.innerText = "Connecting" + "...".substring(2 - timer % 3);
            timer++;
        }, 500);

        this.connector.subscribe("connect", this.onConnect, this, true);
        this.connector.subscribe("disconnect", this.onDisconnect, this, true);
        this.connector.connect();
    }

    onDisable() {
        this.connector.unsubscribe("connect", this.onConnect, this, true);
        this.connector.unsubscribe("disconnect", this.onDisconnect, this, true);

        clearInterval(this.labelTimer);
    }
}
