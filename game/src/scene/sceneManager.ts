import {Scene} from "./scene";

export class SceneManager {
    active?: Scene;

    setScene(scene?: Scene) {
        if (this.active) {
            this.active.disable(scene);
        }
        const old = this.active;
        this.active = scene;
        if (this.active) {
            this.active.enable(old);
        }
    }

    resize() {
        if (this.active) {
            this.active.resize();
        }
    }

    update(delta: number) {
        if (this.active) {
            this.active.update(delta);
        }
    }

    stop() {
        if (this.active) {
            this.active.disable(undefined);
        }
    }
}
