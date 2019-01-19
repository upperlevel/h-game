export interface Scene {
    enable(previous?: Scene): void;

    disable(next?: Scene): void;

    update(delta: number): void;

    resize(): void;
}
