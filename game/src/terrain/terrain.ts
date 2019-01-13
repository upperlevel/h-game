import {GameScene} from "../scenes/game/gameScene";
import {Position} from "../entity/util";

import StaticGroup = Phaser.Physics.Arcade.StaticGroup;
import StaticBody = Phaser.Physics.Arcade.StaticBody;
import Group = Phaser.GameObjects.Group;
import Vector2 = Phaser.Math.Vector2;

export class Terrain {
    private scene: GameScene;

    width: number;
    height: number;

    platformsGroup?: StaticGroup;
    entitiesGroup?: Group;

    spawnPoints: Position[] = [
        {x: 0, y: 0},
        {x: 0, y: 0}
    ];

    constructor(scene: GameScene) {
        this.scene = scene;

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
        this.platformsGroup!.add(platform);

        const body = (platform.body as StaticBody);
        body.position = new Vector2(x, this.height - y - height); // Body's position is top left (top y)
        body.setSize(width, height);

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
        this.scene.physics.world.setBounds(0, 0, this.width, this.height);

        this.platformsGroup = this.scene.physics.add.staticGroup();
        this.entitiesGroup = this.scene.physics.add.group();
        this.scene.physics.add.collider(this.platformsGroup, this.entitiesGroup);

        this.createPlatform(0, 0, this.width, 1, "urban_terrain");
        this.createPlatform(0, 8, 24, 1, "urban_terrain");
        this.createPlatform(this.width - 15, 14, 15, 1, "urban_terrain");
    }

    update(delta: number) {
        this.updateCamera();
    }
}
