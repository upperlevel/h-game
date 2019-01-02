import {PhaseManager, Phases} from "./phases/phase";

export class HGame {
    static instance: HGame = new HGame();
    socket: WebSocket;

    start() {
        const phase = Phases.LOGIN;
        PhaseManager.show(phase);
    }

    dismiss() {
    }
}

HGame.instance.start();
