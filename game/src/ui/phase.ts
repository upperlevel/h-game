import {ConnectingPhase, NoConnectionPhase} from "./connection";
import {LoginPhase} from "./login"
import {LobbyPhase} from "./lobby"
import {GamePhase} from "./game"

import {hgame} from "../index";

export namespace Phases {
    export const CONNECTING:    Phase = new ConnectingPhase();
    export const NO_CONNECTION: Phase = new NoConnectionPhase();
    export const LOGIN:         Phase = new LoginPhase();
    export const LOBBY:         Phase = new LobbyPhase();
    export const GAME:          Phase = new GamePhase();
}

export abstract class Phase {
    abstract name: string;

    show() {
        if (hgame.socket) {
            hgame.socket.onmessage = event => this.onMessage(JSON.parse(event.data))
        }
        this.onShow();
    }

    protected abstract onShow(): void;

    protected abstract onMessage(packet: any): void;

    dismiss() {
        if (hgame.socket) {
            hgame.socket.onmessage = null;
        }
        this.onDismiss();
    }

    protected abstract onDismiss(): void;
}

export class PhaseManager {
    private current: Phase | undefined = undefined;

    show(phase: Phase) {
        if (this.current) {
            this.current.dismiss();
            console.log(`Disabled phase: ${this.current.name}`);
        }
        this.current = phase;
        if (this.current) {
            this.current.show();
            console.log(`Shown phase: ${this.current.name}`);
        }
    }
}
