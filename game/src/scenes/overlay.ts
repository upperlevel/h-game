export class Overlay {
    container: HTMLElement;

    constructor(id: string) {
        this.container = document.getElementById(id) as HTMLElement;
        if (this.container == undefined) {
            throw `Overlay container is null: ${id}`;
        }
    }

    show() {
        this.container.style.display = "block";
        this.onShow();
    }

    onShow() {
    }

    hide() {
        this.onHide();
        this.container.style.display = "none";
    }

    onHide() {
    }
}
