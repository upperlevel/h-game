import {PhaseManager, Phases} from "./phases/phase";

export class HGame {
    static instance: HGame = new HGame();

    phaseManager: PhaseManager = new PhaseManager();
    socket: WebSocket | undefined = undefined;

    start() {
        this.phaseManager.show(Phases.CONNECTING);
    }

    dismiss() {
    }
}

HGame.instance.start();
