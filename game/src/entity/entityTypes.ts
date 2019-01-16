import {EntityType} from "./entityType";

import {SantyType} from "./entities/santy";

export namespace EntityTypes {
    const entities = new Map<string, EntityType>();

    function register(type: EntityType) {
        this.entities.set(type.id, type);
    }

    export function load(onDone: () => void) {
        const loader = PIXI.loader;
        for (const entity of entities.values()) {
            loader.add(entity.spritesheetPath);
        }
        loader.load(() => {
            for (const entity of entities.values()) {
                entity.spritesheet = PIXI.loader.resources[entity.spritesheetPath].spritesheet;
            }
            onDone();
        });
    }

    export function get(id: string) {
        return entities.get(id);
    }

    export const SANTY = new SantyType();

    register(SANTY);
}
