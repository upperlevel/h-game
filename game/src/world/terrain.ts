import {Position} from "./position";

export namespace Terrain {
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
        isCentered: boolean;
        height: number;
        style: PIXI.TextStyleOptions;
    }

    export interface Emitter {
        x: number;
        y: number;
        scale: number;
        textures: string[];
        config: any;
    }

    export interface Decoration {
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
        texts: Text[];
        emitters: Emitter[];
        decorations: Decoration[];
    }
}