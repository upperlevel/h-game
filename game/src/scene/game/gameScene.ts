import {Player} from "../../entity/player/player";
import {Scene} from "../scene";
import {HGame} from "../../index";
import {World} from "../../world/world";
import {EntityTypes} from "../../entity/entityTypes";

export interface GameSceneConfig {
    playerIndex: number,
    playerCount: number,
    playerName: string,
    character: string,
}

export class GameScene implements Scene {
    game: HGame;
    world: World;

    config: GameSceneConfig;

    constructor(game: HGame, config: GameSceneConfig) {
        this.game = game;
        this.config = config;

        this.world = new World(game.app, {
            id: "lobby",

            width: 32,
            height: 18,

            spawnPoints: [],

            platforms: [
                {
                    x: 0,
                    y: 0,
                    width: 32,
                    height: 1,
                    texture: "assets/game/urban_terrain.png"
                }
            ],

            texts: []
        });
    }

    enable() {
        this.world.socket = this.game.gameConnector!;
        this.world.entityRegistry.setup(this.config.playerCount, this.config.playerIndex);

        this.world.setup();

        // Spawn
        let char = EntityTypes.get(this.config.character)!.create(this.world, true) as Player;
        char.name = this.config.playerName;
        char.reloadName();

        this.world.spawn(char);
    }

    update(delta: number): void {
        this.world.update(delta);
    }

    resize() {
        this.world.resize();
    }

    disable() {
        this.world.destroy();
    }
}
