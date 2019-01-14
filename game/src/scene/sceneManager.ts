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
