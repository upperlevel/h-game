import {GameScene} from "../scenes/game/gameScene";
import * as Phaser from "phaser";

type GameObject = Phaser.GameObjects.GameObject;
type Text = Phaser.GameObjects.Text;
type Graphics = Phaser.GameObjects.Graphics;
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

    private lifeContainer: Graphics;
    private lifeBar: Graphics;
    private lifeMaskShape: Graphics;
    private lifeYOffset: number;

    private energyContainer: Graphics;
    private energyBar: Graphics;
    private energyMaskShape: Graphics;
    private energyYOffset: number;

    private lifeMaskOffset = 0;
    private energyMaskOffset = 0;

    private height: number;


    public constructor(scene: GameScene, name: string) {
        const padding = HudRenderer.componentPadding;
        let nextY = 0;

        this.textHud = scene.add.text(0, 0, name, {fontFamily: "pixeled"});
        this.textHud.setOrigin(0.5, 0);
        nextY += this.textHud.height + padding;

        let lifeShapes = HudRenderer.createBar(scene, 0x00ff00);
        this.lifeContainer = lifeShapes[0];
        this.lifeBar = lifeShapes[1];
        this.lifeMaskShape = lifeShapes[2];
        this.lifeYOffset = nextY;
        nextY += HudRenderer.barContainerHeight + padding;

        let energyShapes = HudRenderer.createBar(scene, 0x00BCD4);
        this.energyContainer = energyShapes[0];
        this.energyBar = energyShapes[1];
        this.energyMaskShape = energyShapes[2];
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

        const barOffset = - HudRenderer.barContainerWidth / 2;

        this.textHud.setPosition(x, y);

        this.lifeContainer.setPosition(x + barOffset, y + this.lifeYOffset);
        this.lifeBar.setPosition(x + barOffset, y + this.lifeYOffset);
        this.lifeMaskShape.setPosition(x + barOffset + this.lifeMaskOffset, y + this.lifeYOffset);

        this.energyContainer.setPosition(x + barOffset, y + this.energyYOffset);
        this.energyBar.setPosition(x + barOffset, y + this.energyYOffset);
        this.energyMaskShape.setPosition(x + barOffset + this.energyMaskOffset, y + this.energyYOffset);
    }

    private updateMaskOffsets(life: number, energy: number) {
        this.lifeMaskOffset = -(100 - life * 100) * HudRenderer.scale;
        this.energyMaskOffset = -(100 - energy * 100) * HudRenderer.scale;
    }

    update(body: Body, life: number, energy: number) {
        this.updateMaskOffsets(life, energy);
        this.translateToTarget(body);
    }


    private static createBar(scene: GameScene, colour: number): Graphics[] {
        const scale = HudRenderer.scale;
        const margin = HudRenderer.barContainerMargin;
        const radius = (this.barHeightPixels / 2) * scale;

        let barContainer = scene.add.graphics();
        //barContainer.setOrigin(0.5, 0);
        barContainer.fillStyle(0x444444, 1);
        barContainer.fillRoundedRect(
            0, 0,
            this.barContainerWidth,
            this.barContainerHeight,
            radius
        );

        var bar = scene.add.graphics();
        //bar.setOrigin(0.5, 0);
        bar.fillStyle(colour, 1);
        bar.fillRoundedRect(
            margin * scale,
            margin * scale,
            this.barWidthPixels * scale,
            this.barHeightPixels * scale,
            radius
        );

        // @ts-ignore
        var barMaskShape = scene.make.graphics();
        //barMaskShape.setOrigin(0.5, 0);
        barMaskShape.fillStyle(0xffffff, 1);
        barMaskShape.fillRect(margin * scale, 0, 100 * scale, (10 + margin * 2) * scale);
        bar.setMask(barMaskShape.createGeometryMask());

        return [barContainer, bar, barMaskShape];
    }
}