// @ts-ignore
import {Entity} from "../entity";
import {Player} from "./player";
import Graphics = PIXI.Graphics;
import Sprite = PIXI.Sprite;
import Text = PIXI.Text;
import Texture = PIXI.Texture;
import Point = PIXI.Point;
import TextStyle = PIXI.TextStyle;
import {PlayerHud} from "./playerHud";

export class GamePlayerHud implements PlayerHud {
    static scale = 1 / 48;
    static barContainerMargin = 2;

    static barWidthPixels = 100;
    static barHeightPixels = 10;

    static barContainerWidth = GamePlayerHud.barWidthPixels + 2 * GamePlayerHud.barContainerMargin;
    static barContainerHeight = GamePlayerHud.barHeightPixels + 2 * GamePlayerHud.barContainerMargin;

    static componentPadding = 2 * GamePlayerHud.scale;
    static hudPlayerDistance = -5 * GamePlayerHud.scale;

    static containerTex: Texture;
    static lifeBarTex: Texture;
    static energyBarTex: Texture;
    static barMaskShapeTex: Graphics;

    private textHud: Text;

    private lifeContainer: Sprite;
    private lifeBar: Sprite;
    private lifeMaskShape: Graphics;
    private lifeYOffset: number;

    private energyContainer: Sprite;
    private energyBar: Sprite;
    private energyMaskShape: Graphics;
    private energyYOffset: number;

    private lifeMaskOffset = 0;
    private energyMaskOffset = 0;

    private height: number;


    public constructor(player: Player) {
        GamePlayerHud.load();
        const padding = GamePlayerHud.componentPadding;
        let nextY = 0;
        let scalep = new Point(GamePlayerHud.scale, GamePlayerHud.scale);

        const style = new TextStyle({
            fontFamily: "pixeled",
            fill: player.active ? "lime" : "red",
        });
        this.textHud = new Text(name, style);
        this.textHud.scale = scalep;
        this.textHud.anchor.set(0.5, 0);
        nextY += this.textHud.height + padding;

        this.lifeContainer = new Sprite(GamePlayerHud.containerTex);
        this.lifeContainer.scale = scalep;
        this.lifeContainer.anchor.set(0.5, 0);
        this.lifeBar = new Sprite(GamePlayerHud.lifeBarTex);
        this.lifeBar.scale = scalep;
        this.lifeBar.anchor.set(0.5, 0);
        this.lifeMaskShape = GamePlayerHud.barMaskShapeTex.clone();
        this.lifeMaskShape.scale = scalep;
        //this.lifeMaskShape.anchor.set(0.5, 0);
        this.lifeMaskShape.visible = false;
        this.lifeBar.mask = this.lifeMaskShape;
        this.lifeYOffset = nextY;
        nextY += GamePlayerHud.barContainerHeight / 48 + padding;

        this.energyContainer = new Sprite(GamePlayerHud.containerTex);
        this.energyContainer.scale = scalep;
        this.energyContainer.anchor.set(0.5, 0);
        this.energyBar = new Sprite(GamePlayerHud.energyBarTex);
        this.energyBar.scale = scalep;
        this.energyBar.anchor.set(0.5, 0);
        this.energyMaskShape = GamePlayerHud.barMaskShapeTex.clone();
        this.energyMaskShape.scale = scalep;
        //this.energyMaskShape.anchor.set(0.5, 0);
        this.energyMaskShape.visible = false;
        this.energyBar.mask = this.energyMaskShape;
        this.energyYOffset = nextY;
        nextY +=  GamePlayerHud.barContainerHeight / 48;

        this.height = nextY;

        player.world.app.stage.addChild(
            this.textHud,
            this.lifeContainer, this.lifeBar,
            this.energyContainer, this.energyBar,
            // @ts-ignore (the definitions aren't always right...)
            this.lifeMaskShape, this.energyMaskShape
        );
    }

    setName(name: string) {
        this.textHud.text = name;
    }

    private translateToTarget(target: Entity) {
        let x = target.x;
        let y = target.sprite.y - target.sprite.height - this.height - GamePlayerHud.hudPlayerDistance;
        let barOffY = GamePlayerHud.barContainerMargin * GamePlayerHud.scale;

        this.textHud.position.set(x, y);

        this.lifeContainer.position.set(x, y + this.lifeYOffset);
        this.lifeBar.position.set(x, y + this.lifeYOffset + barOffY);
        this.lifeMaskShape.position.set(x + this.lifeMaskOffset, y + this.lifeYOffset + barOffY);

        this.energyContainer.position.set(x, y + this.energyYOffset);
        this.energyBar.position.set(x, y + this.energyYOffset + barOffY);
        this.energyMaskShape.position.set(x + this.energyMaskOffset, y + this.energyYOffset + barOffY);
    }

    private updateMaskOffsets(life: number, energy: number) {
        const midAdjust = (GamePlayerHud.barContainerWidth / 2) * GamePlayerHud.scale;
        this.lifeMaskOffset = -(100 - life * 100) * GamePlayerHud.scale - midAdjust;
        this.energyMaskOffset = -(100 - energy * 100) * GamePlayerHud.scale - midAdjust;
    }

    update(player: Player) {
        if (player.name != this.textHud.text) {
            this.textHud.text = player.name;
        }
        this.updateMaskOffsets(player.life / player.maxLife, player.energy / player.maxEnergy);
        this.translateToTarget(player);
    }

    private static loaded = false;

    private static load() {
        if (this.loaded) return;
        this.loaded = true;

        const radius = this.barHeightPixels / 2;

        const scaleMode = PIXI.SCALE_MODES.LINEAR;

        let barContainer = new Graphics();
        barContainer.beginFill(0x444444, 1);
        barContainer.drawRoundedRect(
            0, 0,
            this.barContainerWidth,
            this.barContainerHeight,
            radius
        );
        barContainer.endFill();
        this.containerTex = barContainer.generateCanvasTexture(scaleMode);

        let bar = new Graphics();
        bar.beginFill(0x00ff00, 1);
        bar.drawRoundedRect(
            0,
            0,
            GamePlayerHud.barWidthPixels,
            GamePlayerHud.barHeightPixels,
            radius
        );
        bar.endFill();
        this.lifeBarTex = bar.generateCanvasTexture(scaleMode);

        // Draw over previous bar
        bar.beginFill(0x00BCD4, 1);
        bar.drawRoundedRect(
            0,
            0,
            GamePlayerHud.barWidthPixels,
            GamePlayerHud.barHeightPixels,
            radius
        );
        bar.endFill();
        this.energyBarTex = bar.generateCanvasTexture(scaleMode);

        let barMaskShape = new Graphics();
        barMaskShape.beginFill(0xff0000, 1);
        barMaskShape.drawRect(0, 0, this.barContainerWidth, this.barContainerHeight);
        barMaskShape.endFill();
        this.barMaskShapeTex = barMaskShape;
    }

    onDespawn(pl: Player) {
    }
}