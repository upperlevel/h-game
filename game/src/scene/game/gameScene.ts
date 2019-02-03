import {Player} from "../../entity/player/player";
import {Scene} from "../scene";
import {HGame} from "../../index";
import {World} from "../../world/world";
import {EntityTypes} from "../../entity/entityTypes";
import {MobileController} from "../../mobileController";

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

    mobileController?: MobileController;

    constructor(game: HGame, config: GameSceneConfig) {
        this.game = game;
        this.config = config;

        this.world = new World(game.app, {
            id: "lobby",

            width: 32,
            height: 18,

            platforms: [
                {
                    x: 0,
                    y: 0,
                    width: 32,
                    height: 1,
                    texture: "assets/game/urban_terrain.png"
                }
            ],

            texts: [],
        });
    }

    enable() {
        this.world.socket = this.game.gameConnector!;
        this.world.entityRegistry.setup(this.config.playerCount, this.config.playerIndex);

        this.world.setup();

        // Spawn
        let char = EntityTypes.get(this.config.character)!.create(this.world, true) as Player;
        char.x = (this.config.playerIndex + 1) * (this.world.width / (this.config.playerCount + 1));
        char.left = (this.config.playerIndex % 2) != 0;
        char.name = this.config.playerName;

        this.world.spawn(char);

        if (MobileController.isEnabled()) {
            this.mobileController = new MobileController(this.world.app.stage);
        }
    }

    update(delta: number): void {
        this.world.update(delta);
    }

    resize() {
        this.world.resize();
        if (this.mobileController != null) {
            this.mobileController.onResize();
        }
    }

    disable() {
        this.world.destroy();
    }
}
