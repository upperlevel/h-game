import {Text} from "../world/text";
import {World} from "../world/world";
import {linearInterpol, randomInRange, toRadians} from "../util/maths";
import {Popup} from "./popup";
import Sprite = PIXI.Sprite;
import Texture = PIXI.Texture;
import Graphics = PIXI.Graphics;
import {PopupSpawnPacket} from "../protocol";

export interface ComicPopupConfig {
    text: string;
    style: PIXI.TextStyleOptions;

    x: number;
    y: number;
    left: boolean;

    fadeIn: number;
    stay: number;

    minAngle?: number;
    maxAngle?: number;

    minXOffset?: number;
    maxXOffset?: number;
    minYOffset?: number;
    maxYOffset?: number;
}

export class ComicPopup implements Popup {
    x: number;
    y: number;
    left: boolean;
    fadeIn: number;
    stay: number;

    angle: number;
    xOffset: number;
    yOffset: number;

    text: Text;
    line: Sprite;

    totalTime = 0;

    constructor(world: World, data: ComicPopupConfig) {
        ComicPopup.load();

        this.fadeIn = data.fadeIn;
        this.stay = data.stay;
        this.x = data.x;
        this.y = data.y;
        this.left = data.left;

        this.angle = randomInRange(data.minAngle || -20, data.maxAngle || 10);
        this.xOffset = randomInRange(data.minXOffset || -0.2, data.maxXOffset || 0.2);
        this.yOffset = randomInRange(data.minYOffset || -0.01, data.maxYOffset || 0.2);

        this.text = new Text(world, {
            x: this.x,
            y: this.y,
            isCentered: true,
            height: 0.25,
            text: data.text,
            style: data.style,
        });
        this.text.sprite.visible = false;

        this.line = new Sprite(ComicPopup.curve);

        this.line.anchor.set(0, 1);
        this.text.sprite.anchor.set(this.left ? 1 : 0, 1);
        this.line.visible = false;

        this.line.position.set(this.x, world.height - this.y);

        world.mainCamera.addChild(this.line);
    }

    update(delta: number): boolean {
        this.totalTime += delta;

        const scale = 1/48;

        const fadePerc = Math.min(this.totalTime / this.fadeIn, 1);
        const lineFade = Math.min(fadePerc * 2, 1);
        const textFade = Math.max(fadePerc - 0.5, 0) * 2;

        this.line.visible = true;
        this.line.rotation = linearInterpol(lineFade, toRadians(-180), 0);
        const lineScale = linearInterpol(lineFade, 0.1, 1) * scale * 0.2;
        this.line.scale.set(lineScale * (this.left ? -1 : 1), lineScale);

        if (textFade == 0) {
            this.text.sprite.visible = false;
        } else {
            this.text.sprite.visible = true;
            this.text.sprite.rotation = linearInterpol(textFade, toRadians(-90), toRadians(this.angle));
            const textScale = scale * textFade;
            this.text.sprite.scale.set(textScale, textScale);
        }
        this.text.sprite.x = this.x + (this.line.width + this.xOffset) * (this.left ? -1 : 1);
        this.text.sprite.y = this.text.world.height - (this.y + this.line.height + this.yOffset);

        if (this.totalTime > this.fadeIn + this.stay) {
            this.text.remove();
            this.line.parent.removeChild(this.line);
            return true;
        }
        return false;
    }

    toPacket(): PopupSpawnPacket {
        return {
            type: "spawn_popup",
            popupType: "comic",

            x: this.x,
            y: this.y,
            left: this.left,
            fadeIn: this.fadeIn,
            stay: this.stay,

            angle: this.angle,
            xOffset: this.xOffset,
            yOffset: this.yOffset,

            text: this.text.text,
            style: this.text.style,
        }
    }


    private static loaded = false;
    private static curve: Texture;

    private static load() {
        if (this.loaded) return;
        this.loaded = true;

        let g = new Graphics();
        g.lineStyle(4, 0xFFFFFF, 1);
        g.moveTo(0, 100);
        g.quadraticCurveTo(130, 100, 200, 20);
        this.curve = g.generateCanvasTexture();
    }

    static fromPacket(world: World, packet: PopupSpawnPacket): ComicPopup {
        return new ComicPopup(world, {
            x: packet.x,
            y: packet.y,
            left: packet.left,
            fadeIn: packet.fadeIn,
            stay: packet.stay,

            minAngle: packet.angle,
            maxAngle: packet.angle,
            minXOffset: packet.xOffset,
            maxXOffset: packet.xOffset,
            minYOffset: packet.yOffset,
            maxYOffset: packet.yOffset,

            text: packet.text,
            style: packet.style,
        })
    }
}
