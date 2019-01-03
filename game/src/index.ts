import {PhaseManager, Phases} from "./phases/phase";

export class HGame {
    static instance: HGame = new HGame();

    phaseManager: PhaseManager = new PhaseManager();
    socket: WebSocket | undefined = undefined;

    reconnect() {
        this.socket = new WebSocket("ws://localhost:8080/api/matchmaking");

        this.socket!.addEventListener("open", () => {
            HGame.instance.phaseManager.show(Phases.LOGIN);
        });

        this.socket!.addEventListener("close", () => {
            HGame.instance.phaseManager.show(Phases.NO_CONNECTION);
        });
    }

    start() {
        HGame.instance.phaseManager.show(Phases.CONNECTING);
        this.reconnect();
    }

    dismiss() {
    }
}

HGame.instance.start();
