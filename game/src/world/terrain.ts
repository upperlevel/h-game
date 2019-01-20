import {Position} from "./position";

export namespace Terrain {
    export interface Platform {
        x: number;
        y: number;
        width: number;
        height: number;
        texture: string;
    }

    export interface Terrain {
        id: string;

        width: number;
        height: number;

        spawnPoints: Position[];

        platforms: Platform[];
    }
}