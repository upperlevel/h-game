import {GameScene} from "../scenes/game/gameScene";
import * as Phaser from "phaser";
import Scene = Phaser.Scene;

type Text = Phaser.GameObjects.Text;
type Image = Phaser.GameObjects.Image;
type Body = Phaser.Physics.Arcade.Body;

export class HudRenderer {
    static scale = 1;
    static barContainerMargin = 2;

    static barWidthPixels = 100;
    static barHeightPixels = 10;

    static barContainerWidth = (HudRenderer.barWidthPixels + 2 * HudRenderer.barContainerMargin) * HudRenderer.scale;
    static barContainerHeight = (HudRenderer.barHeightPixels + 2 * HudRenderer.barContainerMargin) * HudRenderer.scale;

    static componentPadding = 1;
    static hudPlayerDistance = -5;


    private textHud: Text;

    private lifeContainer: Image;
    private lifeBar: Image;
    private lifeMaskShape: Image;
    private lifeYOffset: number;

    private energyContainer: Image;
    private energyBar: Image;
    private energyMaskShape: Image;
    private energyYOffset: number;

    private lifeMaskOffset = 0;
    private energyMaskOffset = 0;

    private height: number;


    public constructor(scene: GameScene, name: string) {
        HudRenderer.load(scene);
        const padding = HudRenderer.componentPadding;
        let nextY = 0;

        this.textHud = scene.add.text(0, 0, name, {fontFamily: "pixeled"});
        this.textHud.setOrigin(0.5, 0);
        nextY += this.textHud.height + padding;

        this.lifeContainer = scene.add.image(0, 0, "bar_container").setOrigin(0.5, 0);
        this.lifeBar = scene.add.image(0, 0, "life_bar_fill").setOrigin(0.5, 0);
        this.lifeMaskShape = scene.add.image(0, 0, "bar_mask").setOrigin(0.5, 0).setVisible(false);
        this.lifeBar.setMask(this.lifeMaskShape.createBitmapMask());
        this.lifeYOffset = nextY;
        nextY += HudRenderer.barContainerHeight + padding;

        this.energyContainer = scene.add.image(0, 0, "bar_container").setOrigin(0.5, 0);
        this.energyBar = scene.add.image(0, 0, "energy_bar_fill").setOrigin(0.5, 0);
        this.energyMaskShape = scene.add.image(0, 0, "bar_mask").setOrigin(0.5, 0).setVisible(false);
        this.energyBar.setMask(this.energyMaskShape.createBitmapMask());
        this.energyYOffset = nextY;
        nextY +=  HudRenderer.barContainerHeight;

        this.height = nextY;

        console.log(this);
    }

    setName(name: string) {
        this.textHud.setText(name);
    }

    private translateToTarget(target: Body) {
        let x = target.x + target.halfWidth;
        let y = target.y - this.height - HudRenderer.hudPlayerDistance;
        let barOffY = HudRenderer.barContainerMargin * HudRenderer.scale;

        this.textHud.setPosition(x, y);

        this.lifeContainer.setPosition(x, y + this.lifeYOffset);
        this.lifeBar.setPosition(x, y + this.lifeYOffset + barOffY);
        this.lifeMaskShape.setPosition(x + this.lifeMaskOffset, y + this.lifeYOffset + barOffY);

        this.energyContainer.setPosition(x, y + this.energyYOffset);
        this.energyBar.setPosition(x, y + this.energyYOffset + barOffY);
        this.energyMaskShape.setPosition(x + this.energyMaskOffset, y + this.energyYOffset + barOffY);
    }

    private updateMaskOffsets(life: number, energy: number) {
        this.lifeMaskOffset = -(100 - life * 100) * HudRenderer.scale;
        this.energyMaskOffset = -(100 - energy * 100) * HudRenderer.scale;
    }

    update(body: Body, life: number, energy: number) {
        this.updateMaskOffsets(life, energy);
        this.translateToTarget(body);
    }

    private static loaded = false;

    private static load(scene: Scene) {
        if (this.loaded) return;
        this.loaded = true;

        const scale = HudRenderer.scale;
        const margin = HudRenderer.barContainerMargin;
        const radius = (this.barHeightPixels / 2) * scale;

        const make: any = scene.make;

        let barContainer = make.graphics();
        barContainer.fillStyle(0x444444, 1);
        barContainer.fillRoundedRect(
            0, 0,
            this.barContainerWidth,
            this.barContainerHeight,
            radius
        );
        barContainer.generateTexture("bar_container", this.barContainerWidth, this.barContainerHeight);



        let bar = make.graphics();
        bar.fillStyle(0x00ff00, 1);
        bar.fillRoundedRect(
            0,
            0,
            this.barWidthPixels * scale,
            this.barHeightPixels * scale,
            radius
        );
        bar.generateTexture("life_bar_fill", this.barWidthPixels * scale, this.barHeightPixels * scale);

        // Draw over previous bar
        bar.fillStyle(0x00BCD4, 1);
        bar.fillRoundedRect(
            0,
            0,
            this.barWidthPixels * scale,
            this.barHeightPixels * scale,
            radius
        );
        bar.generateTexture("energy_bar_fill", this.barWidthPixels * scale, this.barHeightPixels * scale);

        let barMaskShape = make.graphics();
        barMaskShape.fillStyle(0xffffff, 1);
        barMaskShape.fillRect(0, 0, 100 * scale, (10 + margin * 2) * scale);
        barMaskShape.generateTexture("bar_mask", 100*scale, (10 + margin * 2) * scale);
    }
}