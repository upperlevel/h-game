import {Position} from "../entity/util";


import "planck-js";
import {Player} from "../entity/player";
import {World} from "../world";

export class Terrain {
    private world: World;

    width: number;
    height: number;

    spawnPoints: Position[] = [
        {x: 0, y: 0},
        {x: 0, y: 0}
    ];

    constructor(world: World) {
        this.world = world;

        this.width  = 32;
        this.height = 18;
    }

    /**
     * Creates a platform on this terrain.
     * Coordinates are took starting from bottom-left corner and refers to the bottom-left of the object.
     */
    createPlatform(x: number, y: number, width: number, height: number, texture: string) {
        const image = this.scene.textures.get(texture).getSourceImage();

        // Game objects' position is the middle of it (top y)
        const platform = this.scene.add.tileSprite(x + width / 2, this.height - y - height / 2, width, image.height, texture);
        platform.setScale(1, height / image.height);

        // Body's position is top left (top y)
        let body = this.world.physics.createBody({
           type: "static",
           position: planck.Vec2(x, this.height - y - height + Player.STEP_HEIGHT)
        });
        body.createFixture({
            shape: planck.Edge(planck.Vec2(-width / 2, 0), planck.Vec2(width / 2, height - Player.STEP_HEIGHT))
        });

        return platform;
    }

    private updateCamera() {
        const camera = this.scene.cameras.main;
        const zoom = this.scene.game.canvas.width / this.width;
        camera.setOrigin(0, 0);
        camera.setZoom(zoom);
        camera.setScroll(0, this.height - this.scene.game.canvas.height / zoom);
        camera.setBackgroundColor("#90CAF9");
    }

    load() {
        this.scene.load.image("urban_terrain", "assets/game/urban_terrain.png");
    }

    build() {
        this.updateCamera();

        this.createPlatform(0, 0, this.width, 1, "urban_terrain");
        this.createPlatform(0, 8, 24, 1, "urban_terrain");
        this.createPlatform(this.width - 15, 14, 15, 1, "urban_terrain");
    }

    update(delta: number) {
        this.updateCamera();
    }
}
