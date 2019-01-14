import {Scene} from "./scene";

export class OverlayScene implements Scene {
    container: HTMLElement;

    constructor(id: string) {
        this.container = document.getElementById(id) as HTMLElement;
        if (this.container == undefined) {
            throw `Overlay container is null: ${id}`;
        }
    }

    enable() {
        this.container.style.display = "block";
        this.onEnable();
    }

    protected onEnable() {
    }

    update(delta: number) {
        this.onUpdate(delta);
    }

    protected onUpdate(delta: number) {
    }

    disable() {
        this.onDisable();
        this.container.style.display = "none";
    }

    protected onDisable() {
    }
}
