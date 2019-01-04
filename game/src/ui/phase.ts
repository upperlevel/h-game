import {hgame} from "../index";

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
