import {Position} from "./position";

export namespace Terrain {
    import TextStyleOptions = PIXI.TextStyleOptions;

    export interface Platform {
        x: number;
        y: number;
        width: number;
        height: number;
        texture: string;
    }

    export interface Text {
        text: string;

        x: number;
        y: number;

        centered: boolean;
        height: number;

        style: TextStyleOptions;
    }

    export interface Terrain {
        id: string;

        width: number;
        height: number;

        spawnPoints: Position[];

        platforms: Platform[];
        texts: Text[];
    }
}