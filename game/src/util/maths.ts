
export function toDegrees (angle: number): number {
    return angle * (180 / Math.PI);
}

export function toRadians (angle: number): number {
    return angle * (Math.PI / 180);
}

export function linearInterpol(t: number, a: number, b: number) {
    return a * (t - 1) + b * t;
}

export function randomInRange(min: number, max: number) {
    return Math.random() * (max - min) + min;
}

export function randomInArray<T>(arr: T[]): T {
    return arr[(Math.random() * arr.length) >> 0];
}
